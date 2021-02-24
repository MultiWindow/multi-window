package de.kb1000.multiwindow.client.gui;

import com.raphydaphy.breakoutapi.breakout.Breakout;
import com.raphydaphy.breakoutapi.breakout.window.BreakoutWindow;
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

public class ScreenBreakout extends Breakout {
    private final Screen screen;
    private boolean isClosing;
    private double x;
    private double y;

    public ScreenBreakout(Identifier breakoutId, Screen screen) {
        super(breakoutId, new BreakoutWindow(screen.getTitle().getString(), screen.width, screen.height));
        this.screen = screen;
        // TODO: make these run in render or update instead of on the main thread
        InputUtil.setMouseCallbacks(window.getHandle(),
                (window, x, y) -> MinecraftClient.getInstance().execute(() -> this.onCursorPos(window, x, y)),
                (window, button, action, mods) -> MinecraftClient.getInstance().execute(() -> this.onMouseButton(window, button, action, mods)),
                (window, xOffset, yOffset) -> MinecraftClient.getInstance().execute(() -> this.onMouseScroll(window, xOffset, yOffset)),
                (window, count, names) -> {
                    Path[] paths = new Path[count];

                    for (int j = 0; j < count; ++j) {
                        paths[j] = Paths.get(GLFWDropCallback.getName(names, j));
                    }

                    MinecraftClient.getInstance().execute(() -> this.onFilesDropped(window, Arrays.asList(paths)));
                });
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
        screen.render(new MatrixStack(), (int) x, (int) y, MinecraftClient.getInstance().getLastFrameDuration());

//        MinecraftClient client = MinecraftClient.getInstance();
//        MatrixStack stack = new MatrixStack();
//
//        client.getTextureManager().bindTexture(new Identifier("textures/block/azalea_leaves.png"));
//        DrawableHelper.drawTexture(stack, 50, 250, 0, 0, 0, 180, 300, 32, 32);
//
//        client.textRenderer.draw(stack, "Hello world!", 100 / 3f, 200 / 3f, 0);
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
