/*******************************************************************************
 * Copyright 2012 bmanuel
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
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class MixFilter extends PostProcessorFilter<MixFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Texture1("u_texture1", 0),
        Mix("u_mix", 0);

        private final String mnemonic;
        private int elementSize;

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

    private Texture inputTexture2 = null;
    private float mix = 0.5f;

    public MixFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/mix.frag")));

        rebind();
    }

    public MixFilter setInput(FboWrapper buffer1, FboWrapper buffer2) {
        this.inputTexture = buffer1.getFbo().getColorBufferTexture();
        this.inputTexture2 = buffer2.getFbo().getColorBufferTexture();
        return this;
    }

    public MixFilter setInput(Texture texture1, Texture texture2) {
        this.inputTexture = texture1;
        this.inputTexture2 = texture2;
        return this;
    }

    /** @deprecated use {@link #setInput(FboWrapper, FboWrapper)} instead. */
    @Override
    public MixFilter setInput(FboWrapper input) {
        throw new UnsupportedOperationException("Use #setInput(FboWrapper, FboWrapper)} instead.");
    }

    /** @deprecated use {@link #setInput(Texture, Texture)} instead. */
    @Override
    public MixFilter setInput(Texture input) {
        throw new UnsupportedOperationException("Use #setInput(Texture, Texture)} instead.");
    }

    public float getMix() {
        return mix;
    }

    public void setMix(float mix) {
        this.mix = MathUtils.clamp(0f, 1f, mix);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.Texture1, u_texture1);
        setParams(Param.Mix, mix);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
        inputTexture2.bind(u_texture1);
    }
}
