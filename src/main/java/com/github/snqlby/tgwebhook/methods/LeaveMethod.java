package com.github.snqlby.tgwebhook.methods;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fires if a user has left a room.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LeaveMethod {
  long[] room();
  LeaveReason[] reason() default LeaveReason.ALL;
}
