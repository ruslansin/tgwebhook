package com.github.snqlby.tgwebhook;

import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;

public interface Handler {

  /**
   * Default implementation for handleRequest method.
   *
   * @param update received update
   * @return BotApiMethod object. It is not necessary and can be null, but can speed up your bot.
   */
  default BotApiMethod handleRequest(Update update) {
    if (update.hasChosenInlineQuery()) {
      return onChosenInlineQuery(update);
    } else if (update.hasInlineQuery()) {
      return onInlineQuery(update);
    } else if (update.hasCallbackQuery()) {
      return onCallbackQuery(update);
    } else if (update.hasEditedMessage()) {
      return onEditedMessage(update);
    } else if (update.hasEditedChannelPost()) {
      return onEditedChannelPost(update);
    } else if (update.hasChannelPost()) {
      return onChannelPost(update);
    } else if (update.hasShippingQuery()) {
      return onShippingQuery(update);
    } else if (update.hasPreCheckoutQuery()) {
      return onPreCheckoutQuery(update);
    } else if (update.hasMessage()) {
      return onMessage(update);
    }
    return null;
  }

  BotApiMethod onMessage(Update update);

  BotApiMethod onEditedMessage(Update update);

  BotApiMethod onCallbackQuery(Update update);

  BotApiMethod onInlineQuery(Update update);

  BotApiMethod onChosenInlineQuery(Update update);

  BotApiMethod onChannelPost(Update update);

  BotApiMethod onEditedChannelPost(Update update);

  BotApiMethod onShippingQuery(Update update);

  BotApiMethod onPreCheckoutQuery(Update update);
}