package com.github.snqlby.tgwebhook.methods;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * We use this when we want to filter which fields in the message are of interest to us.
 */
public enum MessageFlag {
  ALL(p -> true),
  TEXT(Message::hasText),
  DOCUMENT(Message::hasDocument),
  VIDEO(Message::hasVideo),
  LOCATION(Message::hasLocation),
  PHOTO(Message::hasPhoto),
  CONTACT(Message::hasContact),
  INVOICE(Message::hasInvoice),
  SUCCESSFUL_PAYMENT(Message::hasSuccessfulPayment),
  AUDIO(p -> p.getAudio() != null),
  STICKER(p -> p.getSticker() != null),
  VOICE(p -> p.getVoice() != null),
  VIDEO_NOTE(p -> p.getVideoNote() != null),
  VENUE(p -> p.getVenue() != null);

  private Predicate<Message> predicate;

  MessageFlag(Predicate<Message> predicate) {
    this.predicate = predicate;
  }

  /**
   * Check if the message contains any declared flag
   * @param message message from update
   * @param method candidate
   * @return true if message contains any declared field
   */
  public static boolean acceptAny(Message message, MessageMethod method) {
    List<MessageFlag> flags = Arrays.asList(method.flag());

    if (flags.contains(ALL)) {
      return true;
    }

    return flags.stream().anyMatch(p -> p.test(message));
  }

  public boolean test(Message message) {
    return predicate.test(message);
  }
}
