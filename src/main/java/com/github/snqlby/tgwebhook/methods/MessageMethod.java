package com.github.snqlby.tgwebhook.methods;

import com.github.snqlby.tgwebhook.Locality;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates a method which will be used for Message requests.</p>
 * <p>The method must contain these args and return back the following:</p>
 * {@code public BotApiMethod yourMethodName(AbsSender,Message)}
 *
 * @see CommandMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MessageMethod {

  /**
   * Locality for a message.
   *
   * @see Locality
   */
  Locality[] locality() default Locality.ALL;

  /**
   * Message must contain any of these fields.
   *
   * @see MessageFlag
   */
  MessageFlag[] flag() default MessageFlag.ALL;
}
