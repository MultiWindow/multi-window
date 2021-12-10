package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.MultiWindowClient;
import de.kb1000.multiwindow.client.gui.ScreenContextTracker;
import de.kb1000.multiwindow.client.gui.ScreenTreeElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MinecraftClientMixin {
    @Shadow @Nullable public Screen currentScreen;
    @Unique
    private final @NotNull List<@NotNull ScreenTreeElement> trees = new ArrayList<>();

    @Unique
    private final @NotNull List<@NotNull Screen> screensOpened = new ArrayList<>();

    @Unique
    private Screen prevCurrentScreen;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void settingScreen(@Nullable Screen screen, CallbackInfo ci) {
        prevCurrentScreen = currentScreen;
        final ScreenContextTracker.ScreenContextElement previousContext = ScreenContextTracker.getCurrentContext();
        if (previousContext != null && previousContext.type == ScreenContextTracker.ScreenContextElement.ScreenEventType.INIT) {
            previousContext.abort = true;
        }
        if (previousContext != null
            && !(previousContext.treeElement.screen instanceof TitleScreen)
            && (screen == null || ((ScreenAccessor) screen).multi_window_getTreeElement() == previousContext.treeElement.parent || screen instanceof TitleScreen)) {
            previousContext.treeElement.markAsClosing();
        }
    }

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init(Lnet/minecraft/client/MinecraftClient;II)V"))
    private void screenInit(Screen screen, MinecraftClient client, int width, int height) {
        final ScreenContextTracker.ScreenContextElement previousContext = ScreenContextTracker.getCurrentContext();

        ScreenTreeElement treeElement = ((ScreenAccessor) screen).multi_window_getTreeElement();
        if (previousContext != null) {
            treeElement.attachTo(previousContext.treeElement);
        }
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.INIT, treeElement);
        screen.init(client, width, height);
        ScreenContextTracker.ScreenContextElement thisContext = ScreenContextTracker.popContext();

        if (screen instanceof TitleScreen) {
            return;
        }

        if (thisContext.abort) {
            return;
        }

        final @NotNull ScreenAccessor screenAccessor = (ScreenAccessor) screen;
        MultiWindowClient.ALL_WINDOWS.add(screenAccessor.multi_window_getWindow());

        currentScreen = prevCurrentScreen;
    }

    @Inject(method = "render", at = @At(value = "INVOKE_STRING", args = "ldc=yield", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
    private void renderWindows(@NotNull CallbackInfo ci) {
        var allWindows = MultiWindowClient.ALL_WINDOWS;

        for (int i = 0; i < allWindows.size(); i++) {
            var window = allWindows.get(i);

            if (window.isClosing()) {
                window.destroy();
                i--;
            } else {
                window.render();
            }
        }
    }
}
