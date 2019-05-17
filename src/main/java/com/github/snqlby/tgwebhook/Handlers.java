package com.github.snqlby.tgwebhook;

import static com.github.snqlby.tgwebhook.utils.AnnotationUtils.getMethodAnnotation;

import com.github.snqlby.tgwebhook.UpdateType.SubUpdateType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide the single place to register handlers.
 *
 * <p>We are using spaces to isolate handler groups from each other. This may be necessary when
 * servicing multiple bots in the same application.</p>
 */
public class Handlers {

  public static final String DEFAULT_SPACE = "default";
  private static final Logger LOG = LoggerFactory.getLogger(Handlers.class);
  private static final Handlers instance = new Handlers();
  private final Map<String, List<HandlerInfo>> handlers;

  private Handlers() {
    this.handlers = new HashMap<>();
  }

  private static Handlers getInstance() {
    return instance;
  }

  /**
   * Add a new handler to default space.
   */
  public static void addHandler(Object object) {
    addHandler(DEFAULT_SPACE, object);
  }

  /**
   * Add a new handler to specified space.
   */
  public static synchronized void addHandler(String space, Object object) {
    getInstance().handlers.putIfAbsent(space, new ArrayList<>());
    List<HandlerInfo> spaceHandlers = getInstance().handlers.get(space);
    spaceHandlers.add(new HandlerInfo(object));
  }

  public static synchronized void clearHandlers(String space) {
    getInstance().handlers.remove(space);
  }

  /**
   * Return handlers for default space.
   *
   * @return empty list if not found
   */
  public static List<HandlerInfo> getHandlers() {
    return getHandlers(DEFAULT_SPACE);
  }

  /**
   * Return handlers for specified space.
   *
   * @return empty list if not found
   */
  public static synchronized List<HandlerInfo> getHandlers(String space) {
    List<HandlerInfo> spaceHandlers = getInstance().handlers.get(space);
    if (spaceHandlers == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(spaceHandlers);
  }

  /**
   * Return presented spaces.
   */
  public static Set<String> getSpaces() {
    return getInstance().handlers.keySet();
  }

  /**
   * Checks if there is at least one class with a Method annotation.
   */
  public static boolean hasAnyMethod(String space, Class<? extends Annotation> method) {
    SubUpdateType methodSubType = SubUpdateType.findByClass(method);
    Optional<UpdateType> methodType = findParent(method);
    List<HandlerInfo> spaceHandlers = getInstance().handlers.get(space);
    if (methodSubType == null || !methodType.isPresent() || spaceHandlers == null) {
      return false;
    }

    for (HandlerInfo handler : spaceHandlers) {
      if (handler.getUpdateTypes().contains(methodType.get()) && handler.getMethodProcessors()
          .containsValue(methodSubType)) {
        return true;
      }
    }
    return false;
  }

  @Deprecated
  public static boolean hasAnyMethod(Class<? extends Annotation> method) {
    return hasAnyMethod(DEFAULT_SPACE, method);
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
      String space, Class<A> type, Predicate<A> predicate) {
    Map<Method, HandlerInfo> result = new HashMap<>();
    final List<HandlerInfo> handlers = getHandlers(space);

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
        A annotation = getMethodAnnotation(type, method.getDeclaringClass(), method);
        if (annotation != null && predicate.test(annotation)) {
          result.put(method, handlerInfo);
        }
      }

    }

    return result;
  }

  public static <A extends Annotation> Map<Method, HandlerInfo> findHandlersForRequest(
      Class<A> type, Predicate<A> predicate) {
    return findHandlersForRequest(DEFAULT_SPACE, type, predicate);
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
}
