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

import com.crashinvaders.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.LensFlare;

/** Lens flare effect.
 * @author Toni Sagrista */
public final class LensFlareEffect extends PostProcessorEffect {

    private final LensFlare lensFlare = new LensFlare();

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
    public void render(FboWrapper src, FboWrapper dest) {
//		restoreViewport(dest);
        lensFlare.setInput(src).setOutput(dest).render();
    }

    public float getIntensity() {
        return lensFlare.getIntensity();
    }

    public void setIntensity(float intensity) {
        lensFlare.setIntensity(intensity);
    }

    public void setColor(float r, float g, float b) {
        lensFlare.setColor(r, g, b);
    }

    /** Sets the light position in screen coordinates [-1..1].
     *
     * @param x Light position x screen coordinate,
     * @param y Light position y screen coordinate. */
    public void setLightPosition(float x, float y) {
        lensFlare.setLightPosition(x, y);
    }
}
