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

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Bias filter.
 * @author Toni Sagrista
 */
public final class BiasFilter extends VfxFilter<BiasFilter> {

    private float bias;

    public enum Param implements Parameter {
        Texture("u_texture0", 0),
        Bias("u_bias", 0);

        final String mnemonic;
        final int elementSize;

        Param(String mnemonic, int elementSize) {
            this.mnemonic = mnemonic;
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

    public BiasFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("bias")));
        rebind();
    }

    public float getBias() {
        return bias;
    }

    public void setBias(float bias) {
        this.bias = bias;
        setParam(Param.Bias, this.bias);
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }

    @Override
    public void resize(int width, int height) {
		// Do nothing.
    }

    @Override
    public void rebind() {
        setParams(Param.Texture, u_texture0);
        setBias(this.bias);
    }
}
