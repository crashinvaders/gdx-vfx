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

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.ChromaticAberrationFilter;

public class ChromaticAberrationEffect extends VfxEffect {

    private final ChromaticAberrationFilter caFilter;

    public ChromaticAberrationEffect() {
        caFilter = new ChromaticAberrationFilter();
    }

    @Override
    public void resize(int width, int height) {
        caFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        caFilter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        caFilter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        caFilter.dispose();
    }
}
