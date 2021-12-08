package de.kb1000.multiwindow.accessor.client;

import de.kb1000.multiwindow.client.gui.ScreenWindow;
import de.kb1000.multiwindow.client.gui.ScreenTreeElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public interface ScreenAccessor {
    @NotNull ScreenWindow multi_window_getWindow();
    @NotNull ScreenTreeElement multi_window_getTreeElement();
}
