/*******************************************************************************
 * Copyright 2012 bmanuel
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

package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.Zoom;

/**
 * Implements a zooming effect.
 */
public final class ZoomerEffect extends VfxEffect {
    private final Zoom zoom;

    /**
     * Creating a Zoomer effect with zoom factor of 1.0.
     */
    public ZoomerEffect() {
        this(1f);
    }

    /**
     * Creating a Zoomer effect.
     * @param zoomFactor initial zoom factor.
     */
    public ZoomerEffect(float zoomFactor) {
        zoom = new Zoom();
        setZoom(zoomFactor);
    }

    @Override
    public void dispose() {
        zoom.dispose();
    }

    @Override
    public void rebind() {
        zoom.rebind();
    }

    @Override
    public void resize(int width, int height) {
        zoom.resize(width, height);
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        zoom.setInput(src).setOutput(dst).render(mesh);
    }

    /**
     * Specify the zoom origin, in screen normalized coordinates.
     */
    public void setOrigin(float x, float y) {
        zoom.setOrigin(x, y);
    }

    public float getZoom() {
        return 1f / zoom.getZoom();
    }

    public void setZoom(float zoom) {
        this.zoom.setZoom(1f / zoom);
    }
}
