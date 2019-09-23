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
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class Convolve1DFilter extends ShaderVfxFilter {

    private static final String U_TEXTURE = "u_texture0";
    private static final String U_SAMPLE_WEIGHTS = "u_sampleWeights";
    private static final String U_SAMPLE_OFFSETS = "u_sampleOffsets";

    public int length;
    public float[] weights;
    public float[] offsets;

    public Convolve1DFilter(int length) {
        this(length, new float[length], new float[length * 2]);
    }

    public Convolve1DFilter(int length, float[] weightsData) {
        this(length, weightsData, new float[length * 2]);
    }

    public Convolve1DFilter(int length, float[] weightsData, float[] offsets) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/convolve-1d.frag"),
                "#define LENGTH " + length));
        setWeights(length, weightsData, offsets);
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE, TEXTURE_HANDLE0);
        program.setUniform2fv(U_SAMPLE_OFFSETS, offsets, 0, length * 2); // LibGDX asks for number of floats, NOT number of elements.
        program.setUniform1fv(U_SAMPLE_WEIGHTS, weights, 0, length);
        program.end();
    }

    public void setWeights(int length, float[] weights, float[] offsets) {
        this.weights = weights;
        this.length = length;
        this.offsets = offsets;
    }
}
