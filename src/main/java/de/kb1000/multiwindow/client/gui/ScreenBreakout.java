package de.kb1000.multiwindow.client.gui;

import com.raphydaphy.breakoutapi.breakout.Breakout;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFWDropCallback;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Environment(EnvType.CLIENT)
public class ScreenBreakout extends Breakout {
    private final Screen screen;
    private boolean isClosing;
    private double x;
    private double y;
    private final MinecraftClient client;
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public ScreenBreakout(Identifier breakoutId, Screen screen) {
        super(breakoutId, new BreakoutWindow(screen.getTitle().getString(), screen.width, screen.height));
        this.screen = screen;
        this.client = Screens.getClient(screen);
        // TODO: make these run in render or update instead of on the main thread
        InputUtil.setMouseCallbacks(window.getHandle(),
                (window, x, y) -> this.client.execute(() -> this.onCursorPos(window, x, y)),
                (window, button, action, mods) -> this.client.execute(() -> this.onMouseButton(window, button, action, mods)),
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
    }

    private void onMouseScroll(long window, double xOffset, double yOffset) {
    }

    private void onFilesDropped(long window, List<Path> names) {
    }

    @Override
    public void render() {
        super.render();
        while (!queue.isEmpty()) {
            queue.poll().run();
        }
        screen.render(new MatrixStack(), (int) x, (int) y, this.client.getLastFrameDuration());
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
