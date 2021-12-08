package de.kb1000.multiwindow.mixin.client;

import de.kb1000.multiwindow.client.gl.GlContextTracker;
import net.minecraft.client.render.VertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VertexFormat.class)
public class VertexFormatMixin {
    @Inject(method = "getVertexArray", at = @At("HEAD"), cancellable = true)
    private void replaceIfOnOtherContext(CallbackInfoReturnable<Integer> cir) {
        var ctx = GlContextTracker.getCurrentContext();
        if (ctx != null) {
            cir.setReturnValue(ctx.getVertexArrayFor((VertexFormat)(Object) this));
        }
    }
}
