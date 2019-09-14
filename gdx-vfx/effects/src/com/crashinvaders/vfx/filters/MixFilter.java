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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Mixes two frames with a factor of {@link #mixFactor}.
 * Second frame texture should be provided through {@link #setSecondInput(Texture)} prior rendering. */
public final class MixFilter extends ShaderVfxFilter {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TEXTURE1 = "u_texture1";
    private static final String U_MIX = "u_mix";

    private Texture secondTexture = null;
    private float mixFactor = 0.5f;

    public MixFilter(Method method) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/mix.frag"),
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

    @Override
    public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
        if (secondTexture == null) {
            throw new IllegalStateException("Second texture is not set. Use #setSecondInput() prior rendering.");
        }
        secondTexture.bind(TEXTURE_HANDLE1);

        super.render(context, src, dst);
    }

    public void setSecondInput(VfxFrameBuffer buffer) {
        if (buffer != null) {
            setSecondInput(buffer.getFbo().getColorBufferTexture());
        } else {
            setSecondInput((Texture) null);
        }
    }

    public void setSecondInput(Texture texture) {
        secondTexture = texture;
        if (texture != null) {
            setUniform(U_TEXTURE1, TEXTURE_HANDLE1);
        }
    }

    public float getMixFactor() {
        return mixFactor;
    }

    public void setMixFactor(float mixFactor) {
        this.mixFactor = MathUtils.clamp(0f, 1f, mixFactor);
        setUniform(U_MIX, mixFactor);
    }

    /** Defines which function will be used to mix the two frames to produce motion blur effect. */
    public enum Method {
        MAX,
        MIX;
    }
}
