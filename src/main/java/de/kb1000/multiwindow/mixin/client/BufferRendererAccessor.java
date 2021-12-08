package de.kb1000.multiwindow.mixin.client;

import net.minecraft.client.render.BufferRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BufferRenderer.class)
public interface BufferRendererAccessor {
  @Accessor("currentElementBuffer")
  static int getCurrentElementBufferObject() {
    throw new IllegalStateException("Mixin not applied!");
  }

  @Accessor("currentElementBuffer")
  static void setCurrentElementBufferObject(int ebo) {
    throw new IllegalStateException("Mixin not applied!");
  }

  @Accessor("currentVertexArray")
  static int getCurrentVertexArrayObject() {
    throw new IllegalStateException("Mixin not applied!");
  }

  @Accessor("currentVertexArray")
  static void setCurrentVertexArrayObject(int vao) {
    throw new IllegalStateException("Mixin not applied!");
  }

  @Accessor("currentVertexBuffer")
  static int getCurrentVertexBufferObject() {
    throw new IllegalStateException("Mixin not applied!");
  }

  @Accessor("currentVertexBuffer")
  static void setCurrentVertexBufferObject(int vbo) {
    throw new IllegalStateException("Mixin not applied!");
  }
}
