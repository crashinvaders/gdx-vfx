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

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class EarthquakeFilter extends VfxFilter<EarthquakeFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Amount("u_amount", 0),
        Speed("u_speed", 0),
        Time("u_time", 0),
        ;

        final String mnemonic;
        final int elementSize;

        Param(String m, int elementSize) {
            this.mnemonic = m;
            this.elementSize = elementSize;
        }

        @Override
        public String mnemonic() {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize() {
            return this.elementSize;
        }
    }

    private float amount;
    private float speed;
    private float time = 0f;

    public EarthquakeFilter(float amount, float speed) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/earthquake.frag")));
        this.amount = amount;
        this.speed = speed;
        rebind();
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
        setParam(Param.Time, time);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void resize(int width, int height) {
        // Do nothing.
    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.Amount, amount);
        setParams(Param.Speed, speed);
        setParams(Param.Time, time);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}

