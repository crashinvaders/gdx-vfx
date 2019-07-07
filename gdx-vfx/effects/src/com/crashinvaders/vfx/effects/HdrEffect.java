/*******************************************************************************
 * Copyright 2012 tsagrista
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.HdrFilter;

/**
 * Light scattering implementation.
 * @author Toni Sagrista
 */
public final class HdrEffect extends PostProcessorEffect {

    private final HdrFilter filter;

    /** Creates the effect */
    public HdrEffect() {
        filter = new HdrFilter();
    }

    /** Creates the effect */
    public HdrEffect(float exposure, float gamma) {
        filter = new HdrFilter(exposure, gamma);
    }

    @Override
    public void dispose() {
        filter.dispose();
    }

    @Override
    public void rebind() {
        filter.rebind();
    }

    @Override
    public void resize(int width, int height) {
        filter.resize(width, height);
    }

    @Override
    public void render(FboWrapper src, FboWrapper dest) {
        filter.setInput(src).setOutput(dest).render();
    }

    /**
     * Set the exposure
     * @param value The exposure
     */
    public void setExposure(float value) {
        filter.setExposure(value);
    }

    /**
     * Set the gamma
     * @param value The gamma
     */
    public void setGamma(float value) {
        filter.setGamma(value);
    }
}