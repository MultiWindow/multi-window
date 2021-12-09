package de.kb1000.multiwindow.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import de.kb1000.multiwindow.client.gl.events.*;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlDebug;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.glfw.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GlContext {
    private static final SavedGlState MAIN = new SavedGlState();

    static {
        MAIN.glRecord();
    }

    private long handle;
    private int width;
    private int height;
    private final Map<VertexFormat, Integer> vertexArrays = new HashMap<>();
    private final SavedGlState state;

    public final Event<SizeChangedCallback> onSizeChanged = EventFactory.createArrayBacked(SizeChangedCallback.class, handlers -> (width1, height1) -> {
        for (var handler : handlers) {
            handler.onSizeChanged(width1, height1);
        }
    });
    public final Event<MouseMoveCallback> onMouseMove = EventFactory.createArrayBacked(MouseMoveCallback.class, handlers -> (x, y) -> {
        for (var handler : handlers) {
            handler.onMouseMove(x, y);
        }
    });
    public final Event<MouseButtonCallback> onMouseButton = EventFactory.createArrayBacked(MouseButtonCallback.class, handlers -> (button, action, mods) -> {
        for (var handler : handlers) {
            handler.onMouseButton(button, action, mods);
        }
    });
    public final Event<MouseScrollCallback> onMouseScroll = EventFactory.createArrayBacked(MouseScrollCallback.class, handlers -> (xOffset, yOffset) -> {
        for (var handler : handlers) {
            handler.onMouseScroll(xOffset, yOffset);
        }
    });
    public final Event<FilesDroppedCallback> onFilesDropped = EventFactory.createArrayBacked(FilesDroppedCallback.class, handlers -> (files) -> {
        for (var handler : handlers) {
            handler.onFilesDropped(files);
        }
    });

    public GlContext(int width, int height, String name, long sharedWith) {
        this.width = width;
        this.height = height;

        try (var ignored = GlUtils.setContextRaw(NULL)) {
            glfwDefaultWindowHints();
            glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
            glfwWindowHint(GLFW_CONTEXT_CREATION_API, GLFW_NATIVE_CONTEXT_API);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
            handle = GLFW.glfwCreateWindow(width, height, name, NULL, sharedWith);

            if (handle == NULL)
                throw new IllegalStateException("Failed to create GlWindow!");

            GLFW.glfwMakeContextCurrent(handle);
            GlDebug.enableDebug(MinecraftClient.getInstance().options.glDebugVerbosity, true);
            state = new SavedGlState();
            state.glRecord();
        }

        GLFW.glfwSetFramebufferSizeCallback(handle, this::sizeChanged);
        GLFW.glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> {
            onMouseMove.invoker().onMouseMove(xpos, ypos);
        });
        GLFW.glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            onMouseButton.invoker().onMouseButton(button, action, mods);
        });
        GLFW.glfwSetScrollCallback(handle, (window, xoffset, yoffset) -> {
            onMouseScroll.invoker().onMouseScroll(xoffset, yoffset);
        });
        GLFW.glfwSetDropCallback(handle, (window, count, names) -> {
            Path[] paths = new Path[count];

            for (int j = 0; j < count; ++j) {
                paths[j] = Paths.get(GLFWDropCallback.getName(names, j));
            }

            onFilesDropped.invoker().onFilesDropped(paths);
        });
    }

    private void sizeChanged(long window, int width, int height) {
        this.width = width;
        this.height = height;

        onSizeChanged.invoker().onSizeChanged(width, height);
    }

    public void destroy() {
        if (this.handle == 0) {
            throw new IllegalStateException("Trying to destroy context that was already destroyed!");
        }

        try (var ignored = setContext()) {
            for (Integer vertexArray : vertexArrays.values()) {
                GlStateManager._glDeleteVertexArrays(vertexArray);
            }
        }
        GLFW.glfwDestroyWindow(this.handle);
        this.handle = 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getHandle() {
        return handle;
    }

    public SavedGlState getState() {
        return state;
    }

    public int getVertexArrayFor(VertexFormat vertexFormat) {
        return vertexArrays.computeIfAbsent(vertexFormat, fmt -> GlStateManager._glGenVertexArrays());
    }

    public ContextRestorer setContext() {
        long old = GLFW.glfwGetCurrentContext();
        GLFW.glfwMakeContextCurrent(handle);
        var prevCtx = GlContextTracker.getCurrentContext();
        GlContextTracker.pushContext(this);

        if (prevCtx != null) {
            prevCtx.state.record();
        } else {
            MAIN.record();
        }
        state.apply();

        return new ContextRestorer(old);
    }

    public class ContextRestorer implements AutoCloseable {
        private final long oldContext;

        public ContextRestorer(long oldContext) {
            this.oldContext = oldContext;
        }

        @Override
        public void close() {
            GlContextTracker.popContext();
            GLFW.glfwMakeContextCurrent(oldContext);

            state.record();

            var newCtx = GlContextTracker.getCurrentContext();

            if (newCtx != null) {
                newCtx.state.apply();
            } else {
                MAIN.apply();
            }

        }
    }
}
