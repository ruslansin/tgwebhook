package com.github.snqlby.tgwebhook.methods;

import java.util.Arrays;
import java.util.List;

public enum JoinReason {
  ALL, SELF, ADD;

  public static boolean accept(JoinReason updateReason, JoinMethod method) {
    List<JoinReason> methodReasons = Arrays.asList(method.reason());

    return methodReasons.contains(ALL) || methodReasons.contains(updateReason);
  }
}
