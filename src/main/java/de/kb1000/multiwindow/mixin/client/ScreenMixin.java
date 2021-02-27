package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.MultiWindow;
import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.gui.ScreenBreakout;
import de.kb1000.multiwindow.client.gui.ScreenTreeElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public class ScreenMixin implements ScreenAccessor {
    @Unique
    private static long breakoutIdCounter;
    @Unique
    private final @NotNull Identifier breakoutId = new Identifier(MultiWindow.MOD_ID, "screen_" + breakoutIdCounter++);
    @Unique
    private @Nullable ScreenBreakout breakout;
    @Unique
    private final @NotNull ScreenTreeElement treeElement = new ScreenTreeElement(null, (Screen) (Object) this);

    @Override
    public @NotNull Identifier multi_window_getBreakoutId() {
        return breakoutId;
    }

    @Override
    public @NotNull ScreenBreakout multi_window_getBreakout() {
        if (breakout != null && !breakout.isClosing()) {
            return breakout;
        }
        return breakout = new ScreenBreakout(breakoutId, (Screen) (Object) this);
    }

    @Override
    public @NotNull ScreenTreeElement multi_window_getTreeElement() {
        return treeElement;
    }
}
