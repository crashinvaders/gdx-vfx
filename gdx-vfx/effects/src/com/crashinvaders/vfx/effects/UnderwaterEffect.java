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

import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.UnderwaterFilter;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.utils.ScreenQuadMesh;

public class UnderwaterEffect extends VfxEffect implements UpdateableEffect {

    private final UnderwaterFilter filter;

    private float time;

    public UnderwaterEffect() {
        this(1f, 1f);
    }

    public UnderwaterEffect(float amount, float speed) {
        filter = new UnderwaterFilter(amount, speed);
    }

    public float getAmount() {
        return filter.getAmount();
    }

    public void setAmount(float amount) {
        filter.setAmount(amount);
    }

    public float getSpeed() {
        return filter.getSpeed();
    }

    public void setSpeed(float speed) {
        filter.setSpeed(speed);
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

    @Override
    public void dispose() {
        filter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time += delta;
        filter.setTime(time);
    }
}
