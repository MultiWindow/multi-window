package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.client.gui.ScreenBreakout;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsScreen.class)
@Environment(EnvType.CLIENT)
public interface ScreenAccessor {
    Identifier multi_window_getBreakoutId();
    ScreenBreakout multi_window_getBreakout();

    @Accessor
    Screen getParent();
}
