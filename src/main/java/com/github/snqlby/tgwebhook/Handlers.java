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
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Update;

public class Handlers {

  private static final Logger LOG = LoggerFactory.getLogger(Handlers.class);

  private static Handlers instance = null;
  private final List<HandlerInfo> handlers;

  public Handlers() {
    this.handlers = new ArrayList<>();
  }

  /**
   * Singletone for Handlers.
   *
   * @return instance of Handlers
   */
  private static synchronized Handlers getInstance() {
    if (instance == null) {
      instance = new Handlers();
    }

    return instance;
  }

  public static void addHandler(Object object) {
    getInstance().handlers.add(new HandlerInfo(object));
  }

  public static List<HandlerInfo> getHandlers() {
    return getInstance().handlers;
  }

  public static List<HandlerInfo> getHandlers(Update update) {
    return getHandlers();
  }

  /**
   * Checks if there is at least one class with a Method annotation.
   *
   * @return true if at least one class contains a method with Method annotation
   */
  public static boolean hasAnyMethod(Class<? extends Annotation> method) {
    SubUpdateType methodSubType = SubUpdateType.findByClass(method);
    Optional<UpdateType> methodType = findParent(method);
    if (methodSubType == null || !methodType.isPresent()) {
      return false;
    }

    for (HandlerInfo handler : getInstance().handlers) {
      if (handler.getUpdateTypes().contains(methodType.get()) && handler.getMethodProcessors()
          .containsValue(methodSubType)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Finds all suitable handlers for an update request.
   *
   * @param type class of an annotation, for example {@code CommandMethod.class}
   * @param predicate a condition, declared in {@link RequestResolver}
   * @param <A> represents annotation
   * @see AcceptTypes
   * @see RequestResolver
   */
  public static <A extends Annotation> Map<Method, HandlerInfo> findHandlersForRequest(
      Class<A> type, Predicate<A> predicate) {
    Map<Method, HandlerInfo> result = new HashMap<>();
    final List<HandlerInfo> handlers = getHandlers();

    for (HandlerInfo handlerInfo : handlers) {

      Optional<UpdateType> parentType = findParent(type);
      if (!parentType.isPresent()) {
        LOG.error("Can't find the parent type for {}", type);
        break;
      }

      if (!handlerInfo.getUpdateTypes().contains(parentType.get())) {
        continue;
      }

      Class<?> clazz = handlerInfo.getHandler().getClass();

      for (Method method : clazz.getMethods()) {
        A annotation = method.getDeclaredAnnotation(type);
        if (annotation != null && predicate.test(annotation)) {
          result.put(method, handlerInfo);
        }
      }

    }

    return result;
  }

  private static Optional<UpdateType> findParent(Class<? extends Annotation> type) {
    return Stream.of(UpdateType.values()).filter(e -> {
      List<SubUpdateType> subTypes = e.getSubTypes();
      for (SubUpdateType subUpdateType : subTypes) {
        if (subUpdateType.getAnnotation() == type) {
          return true;
        }
      }
      return false;
    }).findFirst();
  }

  public static class HandlerInfo {

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
}
