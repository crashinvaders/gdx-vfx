package com.badlogic.gdx.backends.gwt;

import com.google.gwt.webgl.client.WebGLFramebuffer;

/**
 * This class is placed under "com.badlogic.gdx.backends.gwt" package in order
 * to access package-private fields and internal classes of GwtGL20.
 */
public class GwtGLUtils {
    public static Integer getFrameBufferId(GwtGL20 gwtGL20, WebGLFramebuffer frameBuffer) {
        GwtGL20.IntMap<WebGLFramebuffer> frameBuffers = gwtGL20.frameBuffers;
        for (int i = 0; i < 16; i++) {
            WebGLFramebuffer value = frameBuffers.get(i);
            if (value == frameBuffer) return i;
        }
        return null;
    }
}
