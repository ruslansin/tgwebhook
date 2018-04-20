package com.github.snqlby.tgwebhook.methods;

import java.util.Arrays;
import java.util.List;

public enum LeaveReason {
  ALL, SELF, KICK;

  public static boolean accept(LeaveReason updateReason, LeaveMethod method) {
    List<LeaveReason> methodReasons = Arrays.asList(method.reason());

    return methodReasons.contains(ALL) || methodReasons.contains(updateReason);
  }
}
