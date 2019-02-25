package com.github.snqlby.tgwebhook;

import static com.github.snqlby.tgwebhook.Handlers.hasAnyMethod;
import static com.github.snqlby.tgwebhook.methods.CallbackOrigin.findOrigin;

import com.github.snqlby.tgwebhook.methods.CallbackMethod;
import com.github.snqlby.tgwebhook.methods.CallbackOrigin;
import com.github.snqlby.tgwebhook.methods.ChannelPostMethod;
import com.github.snqlby.tgwebhook.methods.ChosenInlineMethod;
import com.github.snqlby.tgwebhook.methods.CommandMethod;
import com.github.snqlby.tgwebhook.methods.EditedChannelPostMethod;
import com.github.snqlby.tgwebhook.methods.EditedMessageMethod;
import com.github.snqlby.tgwebhook.methods.InlineMethod;
import com.github.snqlby.tgwebhook.methods.JoinMethod;
import com.github.snqlby.tgwebhook.methods.JoinReason;
import com.github.snqlby.tgwebhook.methods.LeaveMethod;
import com.github.snqlby.tgwebhook.methods.LeaveReason;
import com.github.snqlby.tgwebhook.methods.MessageFlag;
import com.github.snqlby.tgwebhook.methods.MessageMethod;
import com.github.snqlby.tgwebhook.methods.PreCheckoutMethod;
import com.github.snqlby.tgwebhook.methods.ShippingMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.LongStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class RequestResolver implements Handler {

  private static final Logger LOG = LoggerFactory.getLogger(RequestResolver.class);
  private final String space;

  private TelegramWebhookBot bot;

  /**
   * Provides a possibility to use custom space for handles.
   *
   * <p>It can be used to support multiple bots per application.</p>
   */
  public RequestResolver(String space, TelegramWebhookBot bot) {
    this.space = space;
    this.bot = bot;
  }

  /**
   * Provides default space for handlers.
   */
  public RequestResolver(TelegramWebhookBot bot) {
    this(Handlers.DEFAULT_SPACE, bot);
  }

  @Override
  public BotApiMethod onMessage(Update update) {
    Message message = update.getMessage();
    Locality updateLocality = findMessageLocality(message);
    if (message.isCommand() && hasAnyMethod(space, CommandMethod.class)) {
      List<String> args = parseArgs(removeCommandPostfix(message.getText()));
      String command = args.remove(0);

      return invokeMethod(CommandMethod.class,
          e -> e.command().equals(command) && Locality.accept(updateLocality, e), bot,
          update.getMessage(), args);
    } else if (message.getNewChatMembers() != null && hasAnyMethod(space, JoinMethod.class)) {
      final long roomId = message.getChatId();
      JoinReason reason = findJoinReason(message);
      return invokeMethod(JoinMethod.class,
          e -> LongStream.of(e.room()).anyMatch(v -> v == JoinMethod.ANY || v == roomId)
              && JoinReason.accept(reason, e),
          bot, message, reason);

    } else if (message.getLeftChatMember() != null && hasAnyMethod(space, LeaveMethod.class)) {
      final long roomId = message.getChatId();
      LeaveReason reason = findLeaveReason(message);
      return invokeMethod(LeaveMethod.class,
          e -> LongStream.of(e.room()).anyMatch(v -> v == LeaveMethod.ANY || v == roomId)
              && LeaveReason.accept(reason, e),
          bot, message, reason);

    }

    return invokeMethod(MessageMethod.class,
        e -> Locality.accept(updateLocality, e) && MessageFlag.acceptAny(message, e), bot,
        update.getMessage());
  }

  @Override
  public BotApiMethod onEditedMessage(Update update) {
    return invokeMethod(EditedMessageMethod.class, bot, update.getMessage());
  }

  @Override
  public BotApiMethod onCallbackQuery(Update update) {
    CallbackQuery query = update.getCallbackQuery();
    CallbackOrigin origin = findOrigin(query);
    return invokeMethod(CallbackMethod.class, e -> {
      Locality updateLocality = null;
      if (origin.equals(CallbackOrigin.MESSAGE)) {
        updateLocality = findMessageLocality(query.getMessage());
      }
      return
          (Arrays.asList(e.data()).contains(query.getData()) || Arrays.asList(e.game_short_name())
              .contains(query.getGameShortName())) && Locality.accept(updateLocality, e)
              && CallbackOrigin.accept(origin, e);
    }, bot, update.getCallbackQuery(), origin);
  }

  private Locality findMessageLocality(Message message) {
    Locality locality = null;

    if (message.isUserMessage()) {
      locality = Locality.PRIVATE;
    } else if (message.isChannelMessage()) {
      locality = Locality.CHANNEL;
    } else if (message.isSuperGroupMessage()) {
      locality = Locality.SUPERGROUP;
    } else if (message.isGroupMessage()) {
      locality = Locality.GROUP;
    }

    return locality;
  }

  @Override
  public BotApiMethod onInlineQuery(Update update) {
    return invokeMethod(InlineMethod.class, bot, update.getInlineQuery());
  }

  @Override
  public BotApiMethod onChosenInlineQuery(Update update) {
    return invokeMethod(ChosenInlineMethod.class, bot, update.getChosenInlineQuery());
  }

  @Override
  public BotApiMethod onChannelPost(Update update) {
    return invokeMethod(ChannelPostMethod.class, bot, update.getChannelPost());
  }

  @Override
  public BotApiMethod onEditedChannelPost(Update update) {
    return invokeMethod(EditedChannelPostMethod.class, bot, update.getEditedChannelPost());
  }

  @Override
  public BotApiMethod onShippingQuery(Update update) {
    return invokeMethod(ShippingMethod.class, bot, update.getShippingQuery());
  }

  @Override
  public BotApiMethod onPreCheckoutQuery(Update update) {
    return invokeMethod(PreCheckoutMethod.class, bot, update.getPreCheckoutQuery());
  }

  private <A extends Annotation> BotApiMethod invokeMethod(Class<A> type, Predicate<A> predicate,
      Object... params) {
    Map<Method, HandlerInfo> handlers = Handlers.findHandlersForRequest(space, type, predicate);
    if (handlers.isEmpty()) {
      LOG.warn("Cannot find a handler for request {}", type);
      return null;
    }
    if (handlers.size() > 1) {
      LOG.warn("Found {} handlers for request. Invoking the first...", handlers.size());
    }

    Entry<Method, HandlerInfo> handler = handlers.entrySet().iterator().next();
    Method method = handler.getKey();
    try {
      return (BotApiMethod) method.invoke(handler.getValue().getHandler(), params);
    } catch (Exception e) {
      LOG.error("Cannot invoke method \"{}\": {}", method.toGenericString(), e.getMessage());
    }

    return null;
  }

  private <A extends Annotation> BotApiMethod invokeMethod(Class<A> type, Object... params) {
    return invokeMethod(type, e -> true, params);
  }

  private String removeCommandPostfix(String command) {
    String postfix = "@" + bot.getBotUsername();
    if (command.contains(postfix)) {
      return command.replaceFirst(postfix, "");
    }
    return command;
  }

  private List<String> parseArgs(String text) {
    List<String> result = new ArrayList<>();
    Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(text);
    while (m.find()) {
      result.add(m.group(1).replaceAll("\"", ""));
    }
    return result;
  }

  private JoinReason findJoinReason(Message message) {
    List<User> newMembers = message.getNewChatMembers();
    int from = message.getFrom().getId();
    return newMembers.get(0).getId() == from ? JoinReason.SELF : JoinReason.ADD;
  }

  private LeaveReason findLeaveReason(Message message) {
    User leftMember = message.getLeftChatMember();
    int from = message.getFrom().getId();
    return leftMember.getId() == from ? LeaveReason.SELF : LeaveReason.KICK;
  }
}
