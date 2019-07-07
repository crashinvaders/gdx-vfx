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

import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.FxaaFilter;

/** Implements the fast approximate anti-aliasing. Very fast and useful for combining with other post-processing effects.
 * @author Toni Sagrista */
public final class FxaaEffect extends PostProcessorEffect {

	private final FxaaFilter fxaaFilter;

    public FxaaEffect() {
        fxaaFilter = new FxaaFilter();
    }

    public FxaaEffect(float fxaaReduceMin, float fxaaReduceMul, float fxaaSpanMax) {
        fxaaFilter = new FxaaFilter(fxaaReduceMin, fxaaReduceMul, fxaaSpanMax);
    }

    @Override
    public void dispose() {
        fxaaFilter.dispose();
    }

    @Override
    public void rebind() {
        fxaaFilter.rebind();
    }

    @Override
    public void resize(int width, int height) {
        fxaaFilter.resize(width, height);
    }

    @Override
    public void render(FboWrapper src, FboWrapper dest) {
        fxaaFilter.setInput(src).setOutput(dest).render();
    }

    /** Sets the span max parameter. The default value is 8.
     * @param value */
    public void setSpanMax(float value) {
        fxaaFilter.setFxaaSpanMax(value);
    }
}
