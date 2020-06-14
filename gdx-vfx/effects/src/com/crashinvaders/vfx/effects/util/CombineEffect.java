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
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Merges two frames with an option to change intensity and saturation for each.
 * After applying saturation and intensity factors, the result frame is <code>src0 * (1.0 - src1)  + src1</code>.
 * <p>
 * If you're looking for rather straightforward way to mix two frames into one, have a look at {@link MixEffect}.
 */
public class CombineEffect extends ShaderVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TEXTURE1 = "u_texture1";
    private static final String U_SOURCE0_INTENSITY = "u_src0Intensity";
    private static final String U_SOURCE0_SATURATION = "u_src0Saturation";
    private static final String U_SOURCE1_INTENSITY = "u_src1Intensity";
    private static final String U_SOURCE1_SATURATION = "u_src1Saturation";

    private float s1i, s1s, s2i, s2s;

    public CombineEffect() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/combine.frag")));
        s1i = 1f;
        s2i = 1f;
        s1s = 1f;
        s2s = 1f;
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformi(U_TEXTURE1, TEXTURE_HANDLE1);
        program.setUniformf(U_SOURCE0_INTENSITY, s1i);
        program.setUniformf(U_SOURCE1_INTENSITY, s2i);
        program.setUniformf(U_SOURCE0_SATURATION, s1s);
        program.setUniformf(U_SOURCE1_SATURATION, s2s);
        program.end();
    }

    public void render(VfxRenderContext context, VfxFrameBuffer src0, VfxFrameBuffer src1, VfxFrameBuffer dst) {
        src0.getTexture().bind(TEXTURE_HANDLE0);
        src1.getTexture().bind(TEXTURE_HANDLE1);
        renderShader(context, dst);
    }

    public float getSource1Intensity() {
        return s1i;
    }

    public void setSource1Intensity(float intensity) {
        s1i = intensity;
        setUniform(U_SOURCE0_INTENSITY, intensity);
    }

    public float getSource2Intensity() {
        return s2i;
    }

    public void setSource2Intensity(float intensity) {
        s2i = intensity;
        setUniform(U_SOURCE1_INTENSITY, intensity);
    }

    public float getSource1Saturation() {
        return s1s;
    }

    public void setSource1Saturation(float saturation) {
        s1s = saturation;
        setUniform(U_SOURCE0_SATURATION, saturation);
    }

    public float getSource2Saturation() {
        return s2s;
    }

    public void setSource2Saturation(float saturation) {
        s2s = saturation;
        setUniform(U_SOURCE1_SATURATION, saturation);
    }
}
