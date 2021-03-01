package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.accessor.client.ScreenAccessor;
import de.kb1000.multiwindow.client.gui.ScreenContextTracker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

@Mixin(Mouse.class)
@Environment(EnvType.CLIENT)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
    private void preOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.MOUSE_BUTTON, ((ScreenAccessor) MinecraftClient.getInstance().currentScreen).multi_window_getTreeElement());
    }

    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void postOnMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        ScreenContextTracker.popContext();
    }

    @Inject(method = "onCursorPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V"))
    private void preOnCursorPos(long window, double x, double y, CallbackInfo ci) {
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.CURSOR_POS, ((ScreenAccessor) MinecraftClient.getInstance().currentScreen).multi_window_getTreeElement());
    }

    @Inject(method = "onCursorPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;wrapScreenError(Ljava/lang/Runnable;Ljava/lang/String;Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void postOnCursorPos(long window, double x, double y, CallbackInfo ci) {
        ScreenContextTracker.popContext();
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z"))
    private void preOnMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.MOUSE_SCROLL, ((ScreenAccessor) MinecraftClient.getInstance().currentScreen).multi_window_getTreeElement());
    }

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseScrolled(DDD)Z", shift = At.Shift.AFTER))
    private void postOnMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        ScreenContextTracker.popContext();
    }

    @Inject(method = "onFilesDropped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;filesDragged(Ljava/util/List;)V"))
    private void preOnFilesDropped(long window, List<Path> paths, CallbackInfo ci) {
        ScreenContextTracker.pushContext(ScreenContextTracker.ScreenContextElement.ScreenEventType.FILES_DROPPED, ((ScreenAccessor) MinecraftClient.getInstance().currentScreen).multi_window_getTreeElement());
    }

    @Inject(method = "onFilesDropped", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;filesDragged(Ljava/util/List;)V", shift = At.Shift.AFTER))
    private void postOnFilesDropped(long window, List<Path> paths, CallbackInfo ci) {
        ScreenContextTracker.popContext();
    }
}
