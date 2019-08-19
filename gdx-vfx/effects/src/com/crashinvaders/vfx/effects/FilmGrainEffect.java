/*
 * ******************************************************************************
 *  * Copyright 2019 metaphore
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *****************************************************************************
 */

package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.utils.ScreenQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.FilmGrainFilter;

public class FilmGrainEffect extends VfxEffect implements UpdateableEffect {

    private final FilmGrainFilter filmGrainFilter;

    private float time = 0f;

    public FilmGrainEffect() {
        filmGrainFilter = new FilmGrainFilter();
    }

    @Override
    public void resize(int width, int height) {
        filmGrainFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        filmGrainFilter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        filmGrainFilter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        filmGrainFilter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time = (this.time + delta) % 1f;
        filmGrainFilter.setSeed(this.time);
    }
}
