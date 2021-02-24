package de.kb1000.multiwindow.mixin.client;

import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.gui.ScreenBreakout;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Inject(method = "openScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V", shift = At.Shift.AFTER), cancellable = true)
    private void openScreenHook(Screen screen, CallbackInfo ci) {
        if (screen instanceof TitleScreen) {
            return;
        }
        final Identifier breakoutId = ((ScreenAccessor) screen).multi_window_getBreakoutId();
        BreakoutAPIClient.openBreakout(breakoutId, new ScreenBreakout(breakoutId, screen));
    }
}
