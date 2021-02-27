package de.kb1000.multiwindow.client.gui;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScreenContextTracker {
    public static class ScreenContextElement {
        public ScreenContextElement(ScreenEventType type, ScreenTreeElement treeElement) {
            this.type = type;
            this.treeElement = treeElement;
        }

        public enum ScreenEventType {
            INIT, RENDER
        }

        public final ScreenEventType type;
        public final ScreenTreeElement treeElement;
        public boolean abort;
    }

    private static final ThreadLocal<Deque<ScreenContextElement>> CONTEXT = ThreadLocal.withInitial(ArrayDeque::new);

    public static ScreenContextElement getCurrentContext() {
        return CONTEXT.get().peek();
    }

    public static ScreenContextElement popContext() {
        return CONTEXT.get().pop();
    }

    public static void pushContext(ScreenContextElement.ScreenEventType type, ScreenTreeElement treeElement) {
        CONTEXT.get().push(new ScreenContextElement(type, treeElement));
    }
}
