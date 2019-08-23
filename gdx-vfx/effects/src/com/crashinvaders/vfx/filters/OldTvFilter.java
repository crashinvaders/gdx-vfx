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
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class OldTvFilter extends VfxFilter<OldTvFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Resolution("u_resolution", 2),
        Time("u_time", 0),
        ;

        final String mnemonic;
        final int elementSize;

        Param(String m, int elementSize) {
            this.mnemonic = m;
            this.elementSize = elementSize;
        }

        @Override
        public String mnemonic () {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize () {
            return this.elementSize;
        }
    }

    private final Vector2 resolution = new Vector2();
    private float time = 0f;

    public OldTvFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/old-tv.frag")));
        rebind();
    }

    public void setTime(float time) {
        this.time = time;
        setParam(Param.Time, time);
    }

    @Override
    public void resize(int width, int height) {
        this.resolution.set(width, height);
        rebind();
    }

    @Override
    public void rebind () {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.Resolution, resolution);
        setParams(Param.Time, time);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
