package de.kb1000.multiwindow.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raphydaphy.breakoutapi.breakout.Breakout;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWDropCallback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

@Environment(EnvType.CLIENT)
public class ScreenBreakout extends Breakout {
    private final @NotNull Screen screen;
    private boolean isClosing;
    private double x;
    private double y;
    private final MinecraftClient client;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private int controlLeftTicks;
    private int activeButton;
    private int field_1796;
    private double glfwTime;

    public ScreenBreakout(Identifier breakoutId, @NotNull Screen screen) {
        super(breakoutId, new BreakoutWindow(screen.getTitle().getString(), screen.width, screen.height));
        this.screen = screen;
        this.client = Screens.getClient(screen);
        // TODO: make these run in render or update instead of on the main thread
        InputUtil.setMouseCallbacks(window.getHandle(),
                (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)),
                (window, button, action, mods) -> this.execute(() -> this.onMouseButton(window, button, action, mods)),
                (window, xOffset, yOffset) -> this.client.execute(() -> this.onMouseScroll(window, xOffset, yOffset)),
                (window, count, names) -> {
                    Path[] paths = new Path[count];

                    for (int j = 0; j < count; ++j) {
                        paths[j] = Paths.get(GLFWDropCallback.getName(names, j));
                    }

                    this.client.execute(() -> this.onFilesDropped(window, Arrays.asList(paths)));
                });
    }

    private void execute(Runnable r) {
        queue.add(r);
    }



    private void onCursorPos(long window, double x, double y) {
        this.x = x;
        this.y = y;
    }

    private void onMouseButton(long window, int button, int action, int mods) {
        if (window == this.client.getWindow().getHandle()) {
            boolean bl = action == GLFW_PRESS;
            if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
                if (bl) {
                    if ((mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL) {
                        button = 1;
                        ++this.controlLeftTicks;
                    }
                } else if (this.controlLeftTicks > 0) {
                    button = 1;
                    --this.controlLeftTicks;
                }
            }

            if (bl) {
                if (this.client.options.touchscreen && this.field_1796++ > 0) {
                    return;
                }

                this.activeButton = button;
                this.glfwTime = GlfwUtil.getTime();
            } else if (this.activeButton != -1) {
                if (this.client.options.touchscreen && --this.field_1796 > 0) {
                    return;
                }

                this.activeButton = -1;
            }

            if (this.client.overlay == null) {
                double d = this.x * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth();
                double e = this.y * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight();
                final int finalButton = button;
                ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.MOUSE_BUTTON, ((ScreenAccessor)screen).multi_window_getTreeElement());
                if (bl) {
                    Screen.wrapScreenError(() -> this.screen.mouseClicked(d, e, finalButton), "mouseClicked event handler", this.screen.getClass().getCanonicalName());
                } else {
                    Screen.wrapScreenError(() -> this.screen.mouseReleased(d, e, finalButton), "mouseReleased event handler", this.screen.getClass().getCanonicalName());
                }
                ScreenContextTracker.popContext();
            }
        }
    }

    private void onMouseScroll(long window, double xOffset, double yOffset) {
    }

    private void onFilesDropped(long window, List<Path> names) {
    }

    @Override
    public void render(MatrixStack stack) {
        super.render(stack);
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
        screen.render(stack, (int) x, (int) y, this.client.getLastFrameDuration());
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (!isClosing) {
            isClosing = true;
            screen.onClose();
        }
    }

    // TODO: make closing a Screen close the coupled ScreenBreakout when isClosing is false
    public boolean isClosing() {
        return isClosing;
    }
}
