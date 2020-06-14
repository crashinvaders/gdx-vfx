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

package com.crashinvaders.vfx.effects.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Simply mixes two frames with a factor of {@link #mixFactor}.
 * <p>
 * Depends on {@link Method} the result will be combined with either:
 * <br><code>max(src0, src1 * mixFactor)</code>
 * <br> or
 * <br><code>mix(src0, src1, mixFactor)</code>
 */
public class MixEffect extends ShaderVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TEXTURE1 = "u_texture1";
    private static final String U_MIX = "u_mix";

    private float mixFactor = 0.5f;

    public MixEffect(Method method) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/mix.frag"),
                "#define METHOD " + method.name()));
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformi(U_TEXTURE1, TEXTURE_HANDLE1);
        program.setUniformf(U_MIX, mixFactor);
        program.end();
    }

    public void render(VfxRenderContext context, VfxFrameBuffer src0, VfxFrameBuffer src1, VfxFrameBuffer dst) {
        src0.getTexture().bind(TEXTURE_HANDLE0);
        src1.getTexture().bind(TEXTURE_HANDLE1);
        renderShader(context, dst);
    }

    public float getMixFactor() {
        return mixFactor;
    }

    public void setMixFactor(float mixFactor) {
        this.mixFactor = MathUtils.clamp(0f, 1f, mixFactor);
        setUniform(U_MIX, mixFactor);
    }

    /** Defines which function will be used to combine mix the two frames. */
    public enum Method {
        MAX,
        MIX;
    }
}
