/*******************************************************************************
 * Copyright 2012 bmanuel
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

package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.filters.RadialBlurFilter;

public final class RadialBlurEffect extends VfxEffectOld {
    private final RadialBlurFilter radialBlur;

    public RadialBlurEffect() {
        this(8);
    }

    public RadialBlurEffect(int passes) {
        radialBlur = new RadialBlurFilter(passes);
    }

    @Override
    public void dispose() {
        radialBlur.dispose();
    }

    @Override
    public void rebind() {
        radialBlur.rebind();
    }

    @Override
    public void resize(int width, int height) {
        radialBlur.resize(width, height);
    }

    @Override
    public void render(ViewportQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        radialBlur.setInput(src).setOutput(dst).render(mesh);
    }

    /** @see RadialBlurFilter#setOrigin(int)  */
    public RadialBlurEffect setOrigin(int align) {
        radialBlur.setOrigin(align);
        return this;
    }

    /** @see RadialBlurFilter#setOrigin(float, float)  */
    public RadialBlurEffect setOrigin(float x, float y) {
        radialBlur.setOrigin(x, y);
        return this;
    }

    public float getZoom() {
        return 1f / radialBlur.getZoom();
    }

    public RadialBlurEffect setZoom(float zoom) {
        radialBlur.setZoom(1f / zoom);
        return this;
    }

    public float getStrength() {
        return radialBlur.getStrength();
    }

    public RadialBlurEffect setStrength(float strength) {
        radialBlur.setStrength(strength);
        return this;
    }
}
