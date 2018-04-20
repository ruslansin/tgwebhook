package com.github.snqlby.tgwebhook;

import com.github.snqlby.tgwebhook.methods.CallbackMethod;
import com.github.snqlby.tgwebhook.methods.CallbackOrigin;
import com.github.snqlby.tgwebhook.methods.CommandMethod;
import com.github.snqlby.tgwebhook.methods.MessageMethod;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * Request origin.
 *
 * <p>We can receive requests from a bot private, any channels or (super)groups.</p>
 */
public enum Locality {
  ALL, PRIVATE, GROUP, SUPERGROUP, CHANNEL;

  /**
   * Tests that method accepts a request locality.
   *
   * @param updateLocality Received locality
   * @param candidateMethod Annotation of a method being inspected
   * @return true if a method's annotation contains Locality from a update
   */
  public static boolean accept(Locality updateLocality, Annotation candidateMethod) {

    if (candidateMethod instanceof CallbackMethod) {
      CallbackMethod method = (CallbackMethod) candidateMethod;
      List<Locality> methodLocalities = Arrays.asList(method.locality());
      List<CallbackOrigin> methodOrigins = Arrays.asList(method.origin());

      // It will only work for CallbackOrigin.MESSAGE, otherwise locality will be ignored
      if (!methodOrigins.contains(CallbackOrigin.MESSAGE)) {
        return true;
      }

      return methodLocalities.contains(ALL) || methodLocalities.contains(updateLocality);
    }  else if (candidateMethod instanceof CommandMethod) {
      CommandMethod method = (CommandMethod) candidateMethod;
      List<Locality> methodLocalities = Arrays.asList(method.locality());
      return methodLocalities.contains(ALL) || methodLocalities.contains(updateLocality);
    } else if (candidateMethod instanceof MessageMethod) {
      MessageMethod method = (MessageMethod) candidateMethod;
      List<Locality> methodLocalities = Arrays.asList(method.locality());
      return methodLocalities.contains(ALL) || methodLocalities.contains(updateLocality);

    }

    // TODO: message types support

    return true;
  }
}
