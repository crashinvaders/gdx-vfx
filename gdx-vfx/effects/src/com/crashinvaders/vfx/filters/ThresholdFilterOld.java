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

public final class ThresholdFilterOld extends VfxFilterOld<ThresholdFilterOld> {

    public enum Param implements Parameter {
        // @formatter:off
        Texture("u_texture0", 0),
        Threshold("treshold", 0),
        ThresholdInvTx("tresholdInvTx", 0);
        // @formatter:on

        private String mnemonic;
        final int elementSize;

        private Param(String mnemonic, int elementSize) {
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

    private float gamma = 0;

    public ThresholdFilterOld() {
        super(VfxGLUtils.compileShader(
        		Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/gamma-threshold.frag")));
        rebind();
    }

    public void setTreshold(float gamma) {
        this.gamma = gamma;
        setParams(Param.Threshold, gamma);
        setParams(Param.ThresholdInvTx, 1f / (1 - gamma)).endParams();
    }

    public float getThreshold() {
        return gamma;
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        setParams(Param.Texture, u_texture0);
        setTreshold(this.gamma);
    }
}
