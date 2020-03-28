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

import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;

public class MultipassEffectWrapper extends AbstractVfxEffect implements ChainVfxEffect {

    private final ChainVfxEffect effect;
    private int passes = 1;

    public MultipassEffectWrapper(ChainVfxEffect effect) {
        this.effect = effect;
    }

    @Override
    public void resize(int width, int height) {
        effect.resize(width, height);
    }

    @Override
    public void update(float delta) {
        effect.update(delta);
    }

    @Override
    public void rebind() {
        effect.rebind();
    }

    @Override
    public void dispose() {
        effect.dispose();
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        // Simply swap buffers to simulate render skip.
        if (passes == 0) {
            buffers.swap();
            return;
        }

        final int finalPasses = this.passes;
        for (int i = 0; i < finalPasses; i++) {
            effect.render(context, buffers);
            if (i < finalPasses - 1) {
                buffers.swap();
            }
        }
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        if (passes < 0) {
            throw new IllegalArgumentException("Passes value cannot be a negative number.");
        }
        this.passes = passes;
    }
}
