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
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class ChromaticAberrationFilterOld extends VfxFilterOld<ChromaticAberrationFilterOld> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        MaxDistortion("u_maxDistortion", 0),
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

    private float maxDistortion = 1.2f;

    public ChromaticAberrationFilterOld(int passes) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/chromatic-aberration.frag"),
                "#define PASSES " + passes));
        rebind();
    }

    public float getMaxDistortion() {
        return maxDistortion;
    }

    public void setMaxDistortion(float maxDistortion) {
        this.maxDistortion = maxDistortion;
        setParam(Param.MaxDistortion, maxDistortion);
    }

    @Override
    public void resize(int width, int height) {
        // Do nothing.
    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.MaxDistortion, maxDistortion);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}
