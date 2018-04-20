package com.github.snqlby.tgwebhook.methods;

import java.util.Arrays;
import java.util.List;
import org.telegram.telegrambots.api.objects.CallbackQuery;

/**
 * The origin of the callback request. Callback buttons can be attached to any message or generated
 * by InlineQuery in chats.
 *
 * <p>Telegram API: If the button that originated the query was attached to a message sent by
 * the bot, the field message will be present. If the button was attached to a message sent via the
 * bot (in inline mode), the field inline_message_id will be present.</p>
 */
public enum CallbackOrigin {
  ALL, INLINE, MESSAGE;

  /**
   * Tests that method can process a callback request.
   *
   * @param updateOrigin Received CallbackQuery's origin
   * @param callbackMethod Annotation of a method being inspected
   * @return true if a method's annotation contains CallbackOrigin from a update
   */
  public static boolean accept(CallbackOrigin updateOrigin, CallbackMethod callbackMethod) {
    List<CallbackOrigin> methodOrigins = Arrays.asList(callbackMethod.origin());

    return methodOrigins.contains(ALL) || methodOrigins.contains(updateOrigin);
  }

  /**
   * Defines CallbackOrigin for a received update.
   *
   * @param query CallbackQuery from a received update
   * @return INLINE if Inline Message ID is present, MESSAGE otherwise
   */
  public static CallbackOrigin findOrigin(CallbackQuery query) {
    return query.getInlineMessageId() != null ? CallbackOrigin.INLINE : CallbackOrigin.MESSAGE;
  }
}
