/*******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * HDR filter.
 * @author Toni Sagrista
 */
public final class HdrFilter extends VfxFilter<HdrFilter> {

    private float exposure;
    private float gamma;

    public enum Param implements VfxFilter.Parameter {
        // @formatter:off
        Texture("u_texture0", 0),
        Exposure("u_exposure", 0),
        Gamma("u_gamma", 0);
        // @formatter:on

        private String mnemonic;
        private int elementSize;

        private Param(String mnemonic, int arrayElementSize) {
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

    public HdrFilter() {
        this(3.0f, 2.2f);
    }

    public HdrFilter(float exposure, float gamma) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/hdr.frag")));
        setExposure(exposure);
        setGamma(gamma);
    }

    @Override
    public void rebind() {
        // reimplement super to batch every parameter
        setParams(Param.Texture, u_texture0);
        setParams(Param.Exposure, exposure);
        setParams(Param.Gamma, gamma);
        endParams();
    }

    @Override
    public void resize(int width, int height) {
        // Do nothing.
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
        setParam(Param.Exposure, this.exposure);
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        setParam(Param.Gamma, this.gamma);
    }
}