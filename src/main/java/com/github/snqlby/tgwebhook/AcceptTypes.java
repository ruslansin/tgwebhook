package com.github.snqlby.tgwebhook;

import com.github.snqlby.tgwebhook.methods.CallbackMethod;
import com.github.snqlby.tgwebhook.methods.ChannelPostMethod;
import com.github.snqlby.tgwebhook.methods.ChosenInlineMethod;
import com.github.snqlby.tgwebhook.methods.CommandMethod;
import com.github.snqlby.tgwebhook.methods.EditedChannelPostMethod;
import com.github.snqlby.tgwebhook.methods.EditedMessageMethod;
import com.github.snqlby.tgwebhook.methods.InlineMethod;
import com.github.snqlby.tgwebhook.methods.JoinMethod;
import com.github.snqlby.tgwebhook.methods.LeaveMethod;
import com.github.snqlby.tgwebhook.methods.MessageMethod;
import com.github.snqlby.tgwebhook.methods.PreCheckoutMethod;
import com.github.snqlby.tgwebhook.methods.ShippingMethod;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a class which will contain handlers for update requests. You can define any methods
 * inside the class, but must follow method signatures if you mark methods with a Method-like
 * annotation.
 *
 * @see CallbackMethod
 * @see ChannelPostMethod
 * @see ChosenInlineMethod
 * @see CommandMethod
 * @see JoinMethod
 * @see LeaveMethod
 * @see EditedChannelPostMethod
 * @see EditedMessageMethod
 * @see InlineMethod
 * @see MessageMethod
 * @see PreCheckoutMethod
 * @see ShippingMethod
 * @see UpdateType
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AcceptTypes {

  /**
   * Supported request types by an annotated class.
   *
   * @see UpdateType
   */
  UpdateType[] value() default UpdateType.MESSAGE;
}
