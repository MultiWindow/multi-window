package de.kb1000.multiwindow.mixin.client;

import com.raphydaphy.breakoutapi.BreakoutAPIClient;
import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.gui.ScreenContextTracker;
import de.kb1000.multiwindow.client.gui.ScreenTreeElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Unique
    private final @NotNull List<@NotNull ScreenTreeElement> trees = new ArrayList<>();

    @Redirect(method = "openScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void screenInit(Screen screen, MinecraftClient client, int width, int height) {
        final ScreenContextTracker.ScreenContextElement previousContext = ScreenContextTracker.getCurrentContext();
        if (previousContext != null && previousContext.type == ScreenContextTracker.ScreenContextElement.ScreenEventType.INIT) {
            previousContext.abort = true;
        }
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.INIT, ((ScreenAccessor) screen).multi_window_getTreeElement());
        ScreenContextTracker.ScreenContextElement thisContext;
        try {
            screen.init(client, width, height);
        } finally {
            thisContext = ScreenContextTracker.popContext();
        }

        if (screen instanceof TitleScreen) {
            return;
        }

        if (thisContext.abort) {
            return;
        }

        final @NotNull ScreenAccessor screenAccessor = (ScreenAccessor) screen;
        final @NotNull Identifier breakoutId = screenAccessor.multi_window_getBreakoutId();
        BreakoutAPIClient.openBreakout(breakoutId, screenAccessor.multi_window_getBreakout());
    }
}
