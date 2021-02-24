package de.kb1000.multiwindow.accessor.client;

import de.kb1000.multiwindow.client.gui.ScreenBreakout;
import net.minecraft.util.Identifier;

public interface ScreenAccessor {
    Identifier multi_window_getBreakoutId();
    ScreenBreakout multi_window_getBreakout();
}
