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
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class CombineFilterOld extends VfxFilterOld<CombineFilterOld> {

    private float s1i, s1s, s2i, s2s;

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Texture1("u_texture1", 0),
        Source1Intensity("u_src1Intensity", 0),
        Source1Saturation("u_src1Saturation", 0),
        Source2Intensity("u_src2Intensity", 0),
        Source2Saturation("u_src2Saturation", 0);

        final String mnemonic;
        final int elementSize;

        private Param(String m, int elementSize) {
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

    public CombineFilterOld() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/combine.frag")));
        s1i = 1f;
        s2i = 1f;
        s1s = 1f;
        s2s = 1f;

        rebind();
    }

    public CombineFilterOld setInput(VfxFrameBuffer buffer1, VfxFrameBuffer buffer2) {
        this.inputTexture = buffer1.getFbo().getColorBufferTexture();
        this.inputTexture2 = buffer2.getFbo().getColorBufferTexture();
        return this;
    }

    public CombineFilterOld setInput(Texture texture1, Texture texture2) {
        this.inputTexture = texture1;
        this.inputTexture2 = texture2;
        return this;
    }

    public void setSource1Intensity(float intensity) {
        s1i = intensity;
        setParam(CombineFilterOld.Param.Source1Intensity, intensity);
    }

    public void setSource2Intensity(float intensity) {
        s2i = intensity;
        setParam(CombineFilterOld.Param.Source2Intensity, intensity);
    }

    public void setSource1Saturation(float saturation) {
        s1s = saturation;
        setParam(CombineFilterOld.Param.Source1Saturation, saturation);
    }

    public void setSource2Saturation(float saturation) {
        s2s = saturation;
        setParam(CombineFilterOld.Param.Source2Saturation, saturation);
    }

    public float getSource1Intensity() {
        return s1i;
    }

    public float getSource2Intensity() {
        return s2i;
    }

    public float getSource1Saturation() {
        return s1s;
    }

    public float getSource2Saturation() {
        return s2s;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.Texture1, u_texture1);
        setParams(Param.Source1Intensity, s1i);
        setParams(Param.Source2Intensity, s2i);
        setParams(Param.Source1Saturation, s1s);
        setParams(Param.Source2Saturation, s2s);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
        inputTexture2.bind(u_texture1);
    }
}
