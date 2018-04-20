package com.github.snqlby.tgwebhook.methods;

import com.github.snqlby.tgwebhook.Locality;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates a method which will be used for Message requests. This annotation takes precedence
 * over MessageMethod. All methods with this annotation will be processed before MessageMethod.</p>
 *
 * <p>The method must contain these args and return back the following:</p>
 * {@code public BotApiMethod yourMethodName(AbsSender,Message)}
 *
 * @see MessageMethod
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandMethod {

  /**
   * The command to be processed, for example "/join" or "/start".
   */
  String command();

  /**
   * Locality for a message.
   *
   * @see Locality
   */
  Locality[] locality() default Locality.ALL;
}
