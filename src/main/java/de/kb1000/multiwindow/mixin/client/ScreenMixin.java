package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.gui.ScreenContextTracker;
import de.kb1000.multiwindow.client.gui.ScreenWindow;
import de.kb1000.multiwindow.client.gui.ScreenTreeElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Screen.class)
@Environment(EnvType.CLIENT)
public class ScreenMixin implements ScreenAccessor {
    @Unique
    private @Nullable ScreenWindow window;
    @Unique
    private @Nullable ScreenTreeElement treeElement;

    @Override
    public @NotNull ScreenWindow multi_window_getWindow() {
        if (window != null && !window.isClosing()) {
            return window;
        }
        return window = new ScreenWindow((Screen) (Object) this);
    }

    @Override
    public @NotNull ScreenTreeElement multi_window_getTreeElement() {
        if (treeElement == null) {
            treeElement = new ScreenTreeElement(ScreenContextTracker.getCurrentContext() == null ? null : ScreenContextTracker.getCurrentContext().treeElement, (Screen) (Object) this);
        }

        return treeElement;
    }
}
