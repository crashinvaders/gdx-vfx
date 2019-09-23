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
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class OldTvFilter extends ShaderVfxFilter {

    private static final String Texture0 = "u_texture0";
    private static final String Resolution = "u_resolution";
    private static final String Time = "u_time";

    private final Vector2 resolution = new Vector2();
    private float time = 0f;

    public OldTvFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/old-tv.frag")));
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.resolution.set(width, height);
        rebind();
    }

    @Override
    public void rebind () {
        super.rebind();
        program.begin();
        program.setUniformi(Texture0, TEXTURE_HANDLE0);
        program.setUniformf(Resolution, resolution);
        program.setUniformf(Time, time);
        program.end();
    }

    public void setTime(float time) {
        this.time = time;
        setUniform(Time, time);
    }
}
