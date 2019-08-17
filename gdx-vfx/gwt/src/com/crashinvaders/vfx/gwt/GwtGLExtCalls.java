package com.crashinvaders.vfx.gwt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtGL20;
import com.badlogic.gdx.backends.gwt.GwtGLUtils;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.crashinvaders.vfx.gl.GLExtMethods;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.webgl.client.WebGLFramebuffer;
import com.google.gwt.webgl.client.WebGLRenderingContext;

public class GwtGLExtCalls implements GLExtMethods {
    private static final GLExtMethods.Viewport tmpViewport = new Viewport();

    public static void initialize() {
        VfxGLUtils.customCalls = new GwtGLExtCalls();
    }

    @Override
    public int getBoundFboHandle() {
        GwtGraphics graphics = (GwtGraphics) Gdx.graphics;
        GwtGL20 gl20 = (GwtGL20) graphics.getGL20();
        WebGLRenderingContext renderingContext = graphics.getContext();
        WebGLFramebuffer frameBuffer = renderingContext.getParametero(WebGLRenderingContext.FRAMEBUFFER_BINDING);

        if (frameBuffer == null) {
            return 0;
        } else {
            return GwtGLUtils.getFrameBufferId(gl20, frameBuffer);
        }
    }

    @Override
    public Viewport getViewport() {
        GwtGraphics graphics = (GwtGraphics) Gdx.graphics;
        WebGLRenderingContext renderingContext = graphics.getContext();
        Int32Array viewport = renderingContext.getParameterv(WebGLRenderingContext.VIEWPORT);
        return tmpViewport.set(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
    }
}
