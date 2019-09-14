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

import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.filters.OldTvFilterOld;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;

public class OldTvEffect extends VfxEffectOld implements UpdateableEffect {

    private final OldTvFilterOld oldTvFilter;

    private float time;

    public OldTvEffect() {
        oldTvFilter = new OldTvFilterOld();
    }

    @Override
    public void resize(int width, int height) {
        oldTvFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        oldTvFilter.rebind();
    }

    @Override
    public void render(ViewportQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dest) {
        oldTvFilter.setInput(src).setOutput(dest).render(mesh);
    }

    @Override
    public void dispose() {
        oldTvFilter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time += delta;
        oldTvFilter.setTime(time);
    }
}
