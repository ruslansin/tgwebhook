package com.github.snqlby.tgwebhook.methods;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Indicates a method which will be used for PreCheckoutQuery requests.</p>
 * <p>The method must contain these args and return back the following:</p>
 * {@code public BotApiMethod yourMethodName(AbsSender,PreCheckoutQuery)}
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreCheckoutMethod {

}