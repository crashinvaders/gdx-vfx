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

package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.utils.ScreenQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.LevelsFilter;

/** Implements brightness, contrast, hue and saturation levels
 * @author tsagrista */
public final class LevelsEffect extends VfxEffect {
    private final LevelsFilter filter;

    /** Creates the effect */
    public LevelsEffect() {
        filter = new LevelsFilter();
    }

    @Override
    public void dispose() {
        filter.dispose();
    }

    @Override
    public void resize(int width, int height) {
        filter.resize(width, height);
    }

    @Override
    public void rebind() {
        filter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        filter.setInput(src).setOutput(dst).render(mesh);
    }

    /** Set the brightness
     * @param value The brightness value in [-1..1] */
    public LevelsEffect setBrightness(float value) {
        filter.setBrightness(value);
        return this;
    }

    /** Set the saturation
     * @param value The saturation value in [0..2] */
    public LevelsEffect setSaturation(float value) {
        filter.setSaturation(value);
        return this;
    }

    /** Set the hue
     * @param value The hue value in [0..2] */
    public LevelsEffect setHue(float value) {
        filter.setHue(value);
        return this;
    }

    /** Set the contrast
     * @param value The contrast value in [0..2] */
    public LevelsEffect setContrast(float value) {
        filter.setContrast(value);
        return this;
    }

    /** Sets the gamma correction value
     * @param value The gamma value in [0..3] */
    public LevelsEffect setGamma(float value) {
        filter.setGamma(value);
        return this;
    }

    public float getBrightness() {
        return filter.getBrightness();
    }

    public float getContrast() {
        return filter.getContrast();
    }

    public float getSaturation() {
        return filter.getSaturation();
    }

    public float getHue() {
        return filter.getHue();
    }

    public float getGamma() {
        return filter.getGamma();
    }
}
