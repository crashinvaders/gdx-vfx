/*******************************************************************************
 * Copyright 2012 bmanuel
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

public final class Convolve1DFilterOld extends VfxFilterOld<Convolve1DFilterOld> {

    public enum Param implements Parameter {
        Texture("u_texture0", 0),
        SampleWeights("SampleWeights", 1),
        SampleOffsets("SampleOffsets", 2), // vec2
        ;

        private String mnemonic;
        final int elementSize;

        Param(String mnemonic, int arrayElementSize) {
            this.mnemonic = mnemonic;
            this.elementSize = arrayElementSize;
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

    public int length;
    public float[] weights;
    public float[] offsets;

    public Convolve1DFilterOld(int length) {
        this(length, new float[length], new float[length * 2]);
    }

    public Convolve1DFilterOld(int length, float[] weights_data) {
        this(length, weights_data, new float[length * 2]);
    }

    public Convolve1DFilterOld(int length, float[] weights_data, float[] offsets) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/convolve-1d.frag"),
                "#define LENGTH " + length));
        setWeights(length, weights_data, offsets);
        rebind();
    }

    public void setWeights(int length, float[] weights, float[] offsets) {
        this.weights = weights;
        this.length = length;
        this.offsets = offsets;
    }

    @Override
    public void dispose() {
        super.dispose();
        weights = null;
        offsets = null;
        length = 0;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        setParams(Param.Texture, u_texture0);
        setParamsv(Param.SampleWeights, weights, 0, length);
        setParamsv(Param.SampleOffsets, offsets, 0, length * 2 /* LibGDX asks for number of floats, NOT number of elements! */);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}
