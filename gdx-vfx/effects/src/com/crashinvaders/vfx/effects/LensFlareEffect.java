/*******************************************************************************
 * Copyright 2012 tsagrista
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

import com.crashinvaders.vfx.utils.ScreenQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.LensFlareFilter;

/** Lens flare effect.
 * @author Toni Sagrista */
public final class LensFlareEffect extends VfxEffect {

    private final LensFlareFilter lensFlare = new LensFlareFilter();

    @Override
    public void dispose() {
        lensFlare.dispose();
    }

    @Override
    public void rebind() {
        lensFlare.rebind();
    }

    @Override
    public void resize(int width, int height) {
        lensFlare.resize(width, height);
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
//		restoreViewport(dest);
        lensFlare.setInput(src).setOutput(dst).render(mesh);
    }

    public float getIntensity() {
        return lensFlare.getIntensity();
    }

    public LensFlareEffect setIntensity(float intensity) {
        lensFlare.setIntensity(intensity);
        return this;
    }

    public LensFlareEffect setColor(float r, float g, float b) {
        lensFlare.setColor(r, g, b);
        return this;
    }

    /** Sets the light position in screen normalized coordinates [0..1].
     * @param x Light position x screen coordinate,
     * @param y Light position y screen coordinate. */
    public LensFlareEffect setLightPosition(float x, float y) {
        lensFlare.setLightPosition(x, y);
        return this;
    }
}
