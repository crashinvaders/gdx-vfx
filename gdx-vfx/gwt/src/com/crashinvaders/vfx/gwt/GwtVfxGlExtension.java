/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.gwt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtGL20;
import com.badlogic.gdx.backends.gwt.GwtGraphics;
import com.crashinvaders.vfx.gl.VfxGlExtension;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.google.gwt.typedarrays.shared.Int32Array;
import com.google.gwt.webgl.client.WebGLFramebuffer;
import com.google.gwt.webgl.client.WebGLRenderingContext;

public class GwtVfxGlExtension implements VfxGlExtension {
    private static final VfxGlExtension.Viewport tmpViewport = new Viewport();

    @Override
    public Viewport getViewport() {
        GwtGraphics graphics = (GwtGraphics) Gdx.graphics;
        WebGLRenderingContext renderingContext = graphics.getContext();
        Int32Array viewport = renderingContext.getParameterv(WebGLRenderingContext.VIEWPORT);
        return tmpViewport.set(viewport.get(0), viewport.get(1), viewport.get(2), viewport.get(3));
    }

    @Override
    public int getBoundFboHandle() {
        GwtGraphics graphics = (GwtGraphics) Gdx.graphics;
        GwtGL20 gwtGl = (GwtGL20) graphics.getGL20();
        WebGLRenderingContext renderingContext = graphics.getContext();
        WebGLFramebuffer frameBuffer = renderingContext.getParametero(WebGLRenderingContext.FRAMEBUFFER_BINDING);

        if (frameBuffer == null) {
            return 0;
        } else {
            return getFrameBufferId(gwtGl, frameBuffer);
        }
    }

    private static native int getFrameBufferId(GwtGL20 gwtGl, WebGLFramebuffer frameBuffer) /*-{
        // Access GwtGL20#frameBuffers field.
        var frameBuffers = gwtGl.@com.badlogic.gdx.backends.gwt.GwtGL20::frameBuffers;

        // Check if frame buffer ID was cached previously.
        if (frameBuffer.frameBufferId) {
            return frameBuffer.frameBufferId;
        }

        // Lookup for ID through entire LibGDX GWT frame buffer index.
        for (i = 0; i < frameBuffers.length; i++) {
            if (frameBuffer === frameBuffers[i]) {
                // Cache frame buffer ID inside the javascript object.
                frameBuffer.frameBufferId = i;
                return i;
            }
        }

        throw "Failed to find frame buffer ID."
    }-*/;
}
