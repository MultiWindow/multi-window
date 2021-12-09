package de.kb1000.multiwindow.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import de.kb1000.multiwindow.mixin.client.BufferRendererAccessor;
import de.kb1000.multiwindow.mixin.client.CapabilityTrackerAccessor;
import de.kb1000.multiwindow.mixin.client.GlStateManagerAccessor;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL32C.*;

public class SavedGlState {
    private boolean blendEnabled;
    private int blendSrcFactorRGB;
    private int blendDstFactorRGB;
    private int blendSrcFactorAlpha;
    private int blendDstFactorAlpha;

    private int depthFunc;
    private boolean depthMask;
    private boolean depthTestEnabled;

    private boolean cullEnabled;
    private int cullMode;

    private boolean polyOffsetFill;
    private boolean polyOffsetLine;
    private float polyOffsetFactor;
    private float polyOffsetUnits;

    private boolean colorLogicEnabled;
    private int colorLogicOp;

    private int stencilSubFunc;
    private int stencilSubRef;
    private int stencilSubMask;
    private int stencilMask;
    private int stencilSfail;
    private int stencilDpfail;
    private int stencilDppass;

    private boolean scissorTestEnabled;

    private int activeTexture;
    private final boolean[] texturesEnabled = new boolean[12];
    private final int[] texturesBound = new int[12];

    private boolean colorMaskRed;
    private boolean colorMaskGreen;
    private boolean colorMaskBlue;
    private boolean colorMaskAlpha;

    private int currentVertexArrayObject;
    private int currentVertexBufferObject;
    private int currentElementBufferObject;

    public void glRecord() {
        blendEnabled = glIsEnabled(GL_BLEND);
        blendSrcFactorRGB = glGetInteger(GL_BLEND_SRC_RGB);
        blendDstFactorRGB = glGetInteger(GL_BLEND_DST_RGB);
        blendSrcFactorAlpha = glGetInteger(GL_BLEND_SRC_ALPHA);
        blendDstFactorAlpha = glGetInteger(GL_BLEND_DST_ALPHA);

        depthFunc = glGetInteger(GL_DEPTH_FUNC);
        depthMask = glGetBoolean(GL_DEPTH_WRITEMASK);
        depthTestEnabled = glIsEnabled(GL_DEPTH_TEST);

        cullEnabled = glIsEnabled(GL_CULL_FACE);
        cullMode = glGetInteger(GL_CULL_FACE_MODE);

        polyOffsetFill = glIsEnabled(GL_POLYGON_OFFSET_FILL);
        polyOffsetLine = glIsEnabled(GL_POLYGON_OFFSET_LINE);
        polyOffsetFactor = glGetFloat(GL_POLYGON_OFFSET_FACTOR);
        polyOffsetUnits = glGetFloat(GL_POLYGON_OFFSET_UNITS);

        colorLogicEnabled = glIsEnabled(GL_COLOR_LOGIC_OP);
        colorLogicOp = glGetInteger(GL_LOGIC_OP_MODE);

        stencilSubFunc = glGetInteger(GL_STENCIL_FUNC);
        stencilSubRef = glGetInteger(GL_STENCIL_REF);
        stencilSubMask = glGetInteger(GL_STENCIL_VALUE_MASK);
        stencilMask = glGetInteger(GL_STENCIL_WRITEMASK);
        stencilSfail = glGetInteger(GL_STENCIL_FAIL);
        stencilDpfail = glGetInteger(GL_STENCIL_PASS_DEPTH_FAIL);
        stencilDppass = glGetInteger(GL_STENCIL_PASS_DEPTH_PASS);

        scissorTestEnabled = glIsEnabled(GL_SCISSOR_TEST);

        activeTexture = glGetInteger(GL_ACTIVE_TEXTURE) - GL_TEXTURE0;

//    GlStateManager.Texture2DState[] textures = GlStateManagerAccessor.getTextures();
//    for (int i = 0; i < 12; i++) {
//      texturesBound[i] = textures[i].boundTexture;
//      texturesEnabled[i] = textures[i].capState;
//    }

        ByteBuffer buf = MemoryUtil.memAlloc(4);

        glGetBooleanv(GL_COLOR_WRITEMASK, buf);

        colorMaskRed = buf.get(0) != 0;
        colorMaskGreen = buf.get(1) != 0;
        colorMaskBlue = buf.get(2) != 0;
        colorMaskAlpha = buf.get(3) != 0;

        MemoryUtil.memFree(buf);

        currentElementBufferObject = glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING);
        currentVertexArrayObject = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        currentVertexBufferObject = glGetInteger(GL_ARRAY_BUFFER_BINDING);
    }

    public void record() {
        GlStateManager.BlendFuncState blend = GlStateManagerAccessor.getBlend();
        blendEnabled = ((CapabilityTrackerAccessor)blend.capState).getState();
        blendSrcFactorRGB = blend.srcFactorRGB;
        blendDstFactorRGB = blend.dstFactorRGB;
        blendSrcFactorAlpha = blend.srcFactorAlpha;
        blendDstFactorAlpha = blend.dstFactorAlpha;

        GlStateManager.DepthTestState depth = GlStateManagerAccessor.getDepth();
        depthFunc = depth.func;
        depthMask = depth.mask;
        depthTestEnabled = ((CapabilityTrackerAccessor) depth.capState).getState();

        GlStateManager.CullFaceState cull = GlStateManagerAccessor.getCull();
        cullEnabled = ((CapabilityTrackerAccessor)cull.capState).getState();
        cullMode = cull.mode;

        GlStateManager.PolygonOffsetState polyOffset = GlStateManagerAccessor.getPolyOffset();
        polyOffsetFill = ((CapabilityTrackerAccessor)polyOffset.capFill).getState();
        polyOffsetLine = ((CapabilityTrackerAccessor)polyOffset.capLine).getState();
        polyOffsetFactor = polyOffset.factor;
        polyOffsetUnits = polyOffset.units;

        GlStateManager.LogicOpState colorLogic = GlStateManagerAccessor.getColorLogic();
        colorLogicEnabled = ((CapabilityTrackerAccessor)colorLogic.capState).getState();
        colorLogicOp = colorLogic.op;

        GlStateManager.StencilState stencil = GlStateManagerAccessor.getStencil();
        stencilSubFunc = stencil.subState.func;
        stencilSubRef = stencil.subState.ref;
        stencilSubMask = stencil.subState.mask;
        stencilMask = stencil.mask;
        stencilSfail = stencil.sfail;
        stencilDpfail = stencil.dpfail;
        stencilDppass = stencil.dppass;

        GlStateManager.ScissorTestState scissorTest = GlStateManagerAccessor.getScissorTest();
        scissorTestEnabled = ((CapabilityTrackerAccessor)scissorTest.capState).getState();

        activeTexture = GlStateManagerAccessor.getActiveTexture();

        GlStateManager.Texture2DState[] textures = GlStateManagerAccessor.getTextures();
        for (int i = 0; i < 12; i++) {
            texturesBound[i] = textures[i].boundTexture;
            texturesEnabled[i] = textures[i].capState;
        }

        GlStateManager.ColorMask colorMask = GlStateManagerAccessor.getColorMask();
        colorMaskRed = colorMask.red;
        colorMaskGreen = colorMask.green;
        colorMaskBlue = colorMask.blue;
        colorMaskAlpha = colorMask.alpha;

        currentElementBufferObject = BufferRendererAccessor.getCurrentElementBufferObject();
        currentVertexArrayObject = BufferRendererAccessor.getCurrentVertexArrayObject();
        currentVertexBufferObject = BufferRendererAccessor.getCurrentVertexBufferObject();
    }

    public void apply() {
        GlStateManager.BlendFuncState blend = GlStateManagerAccessor.getBlend();
        ((CapabilityTrackerAccessor)blend.capState).setStateInternal(blendEnabled);
        blend.srcFactorRGB = blendSrcFactorRGB;
        blend.dstFactorRGB = blendDstFactorRGB;
        blend.srcFactorAlpha = blendSrcFactorAlpha;
        blend.dstFactorAlpha = blendDstFactorAlpha;

        GlStateManager.DepthTestState depth = GlStateManagerAccessor.getDepth();
        depth.func = depthFunc;
        depth.mask = depthMask;
        ((CapabilityTrackerAccessor)depth.capState).setStateInternal(depthTestEnabled);

        GlStateManager.CullFaceState cull = GlStateManagerAccessor.getCull();
        ((CapabilityTrackerAccessor)cull.capState).setStateInternal(cullEnabled);
        cull.mode = cullMode;

        GlStateManager.PolygonOffsetState polyOffset = GlStateManagerAccessor.getPolyOffset();
        ((CapabilityTrackerAccessor)polyOffset.capFill).setStateInternal(polyOffsetFill);
        ((CapabilityTrackerAccessor)polyOffset.capLine).setStateInternal(polyOffsetLine);
        polyOffset.factor = polyOffsetFactor;
        polyOffset.units = polyOffsetUnits;

        GlStateManager.LogicOpState colorLogic = GlStateManagerAccessor.getColorLogic();
        ((CapabilityTrackerAccessor)colorLogic.capState).setStateInternal(colorLogicEnabled);
        colorLogic.op = colorLogicOp;

        GlStateManager.StencilState stencil = GlStateManagerAccessor.getStencil();
        stencil.subState.func = stencilSubFunc;
        stencil.subState.ref = stencilSubRef;
        stencil.subState.mask = stencilSubMask;
        stencil.mask = stencilMask;
        stencil.sfail = stencilSfail;
        stencil.dpfail = stencilDpfail;
        stencil.dppass = stencilDppass;

        GlStateManager.ScissorTestState scissorTest = GlStateManagerAccessor.getScissorTest();
        ((CapabilityTrackerAccessor)scissorTest.capState).setStateInternal(scissorTestEnabled);

        GlStateManagerAccessor.setActiveTexture(activeTexture);

        GlStateManager.Texture2DState[] textures = GlStateManagerAccessor.getTextures();
        for (int i = 0; i < 12; i++) {
            textures[i].boundTexture = texturesBound[i];
            textures[i].capState = texturesEnabled[i];
        }

        GlStateManager.ColorMask colorMask = GlStateManagerAccessor.getColorMask();
        colorMask.red = colorMaskRed;
        colorMask.green = colorMaskGreen;
        colorMask.blue = colorMaskBlue;
        colorMask.alpha = colorMaskAlpha;

        BufferRendererAccessor.setCurrentElementBufferObject(currentElementBufferObject);
        BufferRendererAccessor.setCurrentVertexArrayObject(currentVertexArrayObject);
        BufferRendererAccessor.setCurrentVertexBufferObject(currentVertexBufferObject);
    }

    public static void dump() {

    }
}
