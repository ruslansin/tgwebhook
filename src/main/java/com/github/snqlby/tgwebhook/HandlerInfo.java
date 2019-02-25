package com.github.snqlby.tgwebhook;

import com.github.snqlby.tgwebhook.UpdateType.SubUpdateType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class HandlerInfo {

  private Object handler;
  private List<UpdateType> updateTypes = new ArrayList<>();
  private Map<Method, SubUpdateType> methodProcessors = new HashMap<>();

  /**
   * Stores information about AccessType values and method annotations per class.
   *
   * @param handler a class instance with declared AccessType annotation
   */
  public HandlerInfo(Object handler) {
    this.handler = handler;
    AcceptTypes acceptTypes = handler.getClass().getAnnotation(AcceptTypes.class);
    Optional.ofNullable(acceptTypes).ifPresent(e -> Collections.addAll(updateTypes, e.value()));

    Stream.of(handler.getClass().getMethods()).forEach(e -> {
      Optional<SubUpdateType> processor = Optional.ofNullable(findProcessor(e));
      processor.ifPresent(subUpdateType -> methodProcessors.put(e, subUpdateType));
    });
  }

  /**
   * Searches supported annotation for the the method.
   *
   * @param method method from a class with AccessType annotation
   */
  private SubUpdateType findProcessor(Method method) {
    for (SubUpdateType type : SubUpdateType.values()) {
      for (Annotation annotation : method.getAnnotations()) {
        if (type.getAnnotation().isAssignableFrom(annotation.getClass())) {
          return type;
        }
      }
    }
    return null;
  }

  public Object getHandler() {
    return handler;
  }

  public List<UpdateType> getUpdateTypes() {
    return updateTypes;
  }

  public Map<Method, SubUpdateType> getMethodProcessors() {
    return methodProcessors;
  }

  public Method getMethod() {
    return null;
  }
}