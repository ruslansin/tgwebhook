package com.github.snqlby.tgwebhook.methods;

import com.github.snqlby.tgwebhook.Locality;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates a method which will be used for CallbackQuery requests.</p>
 * <p>The method must contain these args and return back the following:</p>
 * {@code public BotApiMethod yourMethodName(AbsSender,CallbackQuery,CallbackOrigin)}
 *
 *
 * <p>Telegram API: CallbackQuery represents an incoming callback query from a
 * callback button in an inline keyboard. If the button that originated the query was attached to a
 * message sent by the bot, the field message will be present. If the button was attached to a
 * message sent via the bot (in inline mode), the field inline_message_id will be present. Exactly
 * one of the fields data or game_short_name will be present.</p>
 *
 * <p>Telegram API: Note that message content and message date will not be available if the message
 * is too old</p>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallbackMethod {

  /**
   * Accepted CallbackQuery.data values.
   */
  String[] data() default "";

  /**
   * Accepted CallbackQuery.game_short_name values.
   */
  String[] game_short_name() default "";

  /**
   * Locality for request.
   *
   * It will only work for origin() = {@link CallbackOrigin#MESSAGE}, otherwise ignored
   *
   * @see Locality
   */
  Locality[] locality() default Locality.ALL;

  /**
   * The origin of the request.
   *
   * @see CallbackOrigin
   */
  CallbackOrigin[] origin() default CallbackOrigin.ALL;

}
