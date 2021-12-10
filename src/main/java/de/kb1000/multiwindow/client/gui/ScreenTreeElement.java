package de.kb1000.multiwindow.client.gui;

import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ScreenTreeElement {
    public final @NotNull List<@NotNull ScreenTreeElement> children = new ArrayList<>();
    public @Nullable ScreenTreeElement parent;
    public @NotNull Screen screen;

    public ScreenTreeElement(@Nullable ScreenTreeElement parent, @NotNull Screen screen) {
        this.parent = parent;
        this.screen = screen;
    }

    public void markAsClosing() {
        markAsClosingInner();

        if (parent != null) {
            parent.children.remove(this);
        }
    }

    private void markAsClosingInner() {
        for (var child : children) {
            child.markAsClosingInner();
        }

        children.clear();

        ScreenAccessor screenAccessor = (ScreenAccessor) screen;
        if (screenAccessor.multi_window_hasWindow()) {
            screenAccessor.multi_window_getWindow().markAsClosing();
        }
    }

    public void attachTo(ScreenTreeElement parent) {
        this.parent = parent;
        parent.children.add(this);
    }
}
