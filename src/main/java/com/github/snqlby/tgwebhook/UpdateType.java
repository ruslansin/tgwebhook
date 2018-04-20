package com.github.snqlby.tgwebhook;

import com.github.snqlby.tgwebhook.methods.CallbackMethod;
import com.github.snqlby.tgwebhook.methods.ChannelPostMethod;
import com.github.snqlby.tgwebhook.methods.ChosenInlineMethod;
import com.github.snqlby.tgwebhook.methods.CommandMethod;
import com.github.snqlby.tgwebhook.methods.EditedChannelMethod;
import com.github.snqlby.tgwebhook.methods.EditedMessageMethod;
import com.github.snqlby.tgwebhook.methods.InlineMethod;
import com.github.snqlby.tgwebhook.methods.JoinMethod;
import com.github.snqlby.tgwebhook.methods.LeaveMethod;
import com.github.snqlby.tgwebhook.methods.MessageMethod;
import com.github.snqlby.tgwebhook.methods.PreCheckoutMethod;
import com.github.snqlby.tgwebhook.methods.ShippingMethod;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public enum UpdateType {
  INLINE_QUERY(SubUpdateType.INLINE_QUERY),
  CHOSEN_INLINE_QUERY(SubUpdateType.CHOSEN_INLINE_QUERY),
  CALLBACK_QUERY(SubUpdateType.CALLBACK_QUERY),
  MESSAGE(SubUpdateType.MESSAGE, SubUpdateType.COMMAND, SubUpdateType.JOIN, SubUpdateType.LEAVE),
  EDITED_MESSAGE(SubUpdateType.EDITED_MESSAGE),
  CHANNEL_POST(SubUpdateType.CHANNEL_POST),
  EDITED_CHANNEL_POST(SubUpdateType.EDITED_CHANNEL_POST),
  SHIPPING_QUERY(SubUpdateType.SHIPPING_QUERY),
  PRE_CHECKOUT_QUERY(SubUpdateType.PRE_CHECKOUT_QUERY);

  private List<SubUpdateType> subTypes;

  UpdateType(SubUpdateType... subTypes) {
    this.subTypes = Arrays.asList(subTypes);
  }

  public List<SubUpdateType> getSubTypes() {
    return subTypes;
  }

  public enum SubUpdateType {
    INLINE_QUERY(InlineMethod.class),
    CHOSEN_INLINE_QUERY(ChosenInlineMethod.class),
    CALLBACK_QUERY(CallbackMethod.class),
    MESSAGE(MessageMethod.class),
    COMMAND(CommandMethod.class),
    JOIN(JoinMethod.class),
    LEAVE(LeaveMethod.class),
    EDITED_MESSAGE(EditedMessageMethod.class),
    CHANNEL_POST(ChannelPostMethod.class),
    EDITED_CHANNEL_POST(EditedChannelMethod.class),
    SHIPPING_QUERY(ShippingMethod.class),
    PRE_CHECKOUT_QUERY(PreCheckoutMethod.class);

    private Class<? extends Annotation> annotation;

    SubUpdateType(Class<? extends Annotation> annotation) {
      this.annotation = annotation;
    }

    /**
     * Finds SubUpdateType element by the linked Annotation class.
     *
     */
    public static SubUpdateType findByClass(Class<? extends Annotation> clazz) {
      for (SubUpdateType type : values()) {
        if (type.getAnnotation().isAssignableFrom(clazz)) {
          return type;
        }
      }
      return null;
    }

    public Class<? extends Annotation> getAnnotation() {
      return annotation;
    }
  }
}
