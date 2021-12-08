package de.kb1000.multiwindow.client.gl;

import java.util.ArrayDeque;
import java.util.Deque;

public class GlContextTracker {
  private static final ThreadLocal<Deque<GlContext>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);

  public static GlContext getCurrentContext() {
    return CONTEXT.get().peek();
  }

  public static void popContext() {
    CONTEXT.get().pop();
  }

  public static void pushContext(GlContext context) {
    CONTEXT.get().push(context);
  }
}
