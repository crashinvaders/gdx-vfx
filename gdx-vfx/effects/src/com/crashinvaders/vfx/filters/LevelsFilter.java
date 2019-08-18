/*******************************************************************************
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
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Controls levels of brightness and contrast
 * @author tsagrista */
public final class LevelsFilter extends VfxFilter<LevelsFilter> {
    private float brightness = 0.0f;
    private float contrast = 1.0f;
    private float saturation = 1.0f;
    private float hue = 1.0f;
    private float gamma = 1.0f;

    public enum Param implements Parameter {
        // @formatter:off
        Texture("u_texture0", 0),
        Brightness("u_brightness", 0),
        Contrast("u_contrast", 0),
        Saturation("u_saturation", 0),
        Hue("u_hue", 0),
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

    public LevelsFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/levels.frag")));
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        // Do nothing.
    }

    @Override
    public void rebind() {
        // reimplement super to batch every parameter
        setParams(Param.Texture, u_texture0);
        setParams(Param.Brightness, brightness);
        setParams(Param.Contrast, contrast);
        setParams(Param.Saturation, saturation);
        setParams(Param.Hue, hue);
        setParams(Param.Gamma, gamma);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }

    /**
     * Sets the contrast level
     * @param contrast The contrast value in [0..2]
     */
    public void setContrast(float contrast) {
        this.contrast = contrast;
        setParam(Param.Contrast, this.contrast);
    }

    /**
     * Sets the brightness level
     * @param brightness The brightness value in [-1..1]
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
        setParam(Param.Brightness, this.brightness);
    }

    /**
     * Sets the saturation
     * @param saturation The saturation level in [0..2]
     */
    public void setSaturation(float saturation) {
        this.saturation = saturation;
        setParam(Param.Saturation, this.saturation);
    }

    /**
     * Sets the hue
     * @param hue The hue level in [0..2]
     */
    public void setHue(float hue) {
        this.hue = hue;
        setParam(Param.Hue, this.hue);
    }

    /**
     * Sets the gamma correction value
     * @param gamma Gamma value in [0..3]
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
        setParam(Param.Gamma, this.gamma);
    }

    public float getBrightness() {
        return brightness;
    }

    public float getContrast() {
        return contrast;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getHue() {
        return hue;
    }

    public float getGamma() {
        return gamma;
    }
}
