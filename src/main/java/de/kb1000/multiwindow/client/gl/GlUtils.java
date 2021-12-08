package de.kb1000.multiwindow.client.gl;

import org.lwjgl.glfw.GLFW;

public class GlUtils {
    public static RawContextRestorer setContextRaw(long handle) {
        long old = GLFW.glfwGetCurrentContext();
        GLFW.glfwMakeContextCurrent(handle);
        return new RawContextRestorer(old);
    }

    public static class RawContextRestorer implements AutoCloseable {
        private final long oldContext;

        private RawContextRestorer(long old) {
            this.oldContext = old;
        }

        @Override
        public void close() {
            GLFW.glfwMakeContextCurrent(oldContext);
        }
    }


}
