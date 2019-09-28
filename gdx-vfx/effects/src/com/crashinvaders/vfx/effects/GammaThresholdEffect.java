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

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Keeps only values brighter than the specified gamma. */
public final class GammaThresholdEffect extends ShaderVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_THRESHOLD = "u_threshold";
    private static final String U_THRESHOLD_INV = "u_thresholdInv";

    private float gamma;

    public GammaThresholdEffect(Type type) {
        super(VfxGLUtils.compileShader(
        		Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/gamma-threshold.frag"),
                "#define THRESHOLD_TYPE " + type.name()));
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_THRESHOLD, gamma);
        program.setUniformf(U_THRESHOLD_INV, 1f / (1f - gamma));
        program.end();
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        setUniform(U_THRESHOLD, gamma);
        setUniform(U_THRESHOLD_INV, 1f / (1f - gamma));
    }

    public float getGamma() {
        return gamma;
    }

    public enum Type {
        RGBA,
        RGB,
        ALPHA_PREMULTIPLIED,
    }
}
