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

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Controls levels of brightness and contrast. */
public class LevelsEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String Texture = "u_texture0";
    private static final String Brightness = "u_brightness";
    private static final String Contrast = "u_contrast";
    private static final String Saturation = "u_saturation";
    private static final String Hue = "u_hue";
    private static final String Gamma = "u_gamma";

    private float brightness = 0.0f;
    private float contrast = 1.0f;
    private float saturation = 1.0f;
    private float hue = 1.0f;
    private float gamma = 1.0f;

    public LevelsEffect() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/levels.frag")));
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(Texture, TEXTURE_HANDLE0);
        program.setUniformf(Brightness, brightness);
        program.setUniformf(Contrast, contrast);
        program.setUniformf(Saturation, saturation);
        program.setUniformf(Hue, hue);
        program.setUniformf(Gamma, gamma);
        program.end();
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        render(context, buffers.getSrcBuffer(), buffers.getDstBuffer());
    }

    public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
        // Bind src buffer's texture as a primary one.
        src.getTexture().bind(TEXTURE_HANDLE0);
        // Apply shader effect and render result to dst buffer.
        renderShader(context, dst);
    }

    public float getContrast() {
        return contrast;
    }

    /**
     * Sets the contrast level
     * @param contrast The contrast value in [0..2]
     */
    public void setContrast(float contrast) {
        this.contrast = contrast;
        setUniform(Contrast, this.contrast);
    }

    public float getBrightness() {
        return brightness;
    }

    /**
     * Sets the brightness level
     * @param brightness The brightness value in [-1..1]
     */
    public void setBrightness(float brightness) {
        this.brightness = brightness;
        setUniform(Brightness, this.brightness);
    }

    public float getSaturation() {
        return saturation;
    }

    /**
     * Sets the saturation
     * @param saturation The saturation level in [0..2]
     */
    public void setSaturation(float saturation) {
        this.saturation = saturation;
        setUniform(Saturation, this.saturation);
    }

    public float getHue() {
        return hue;
    }

    /**
     * Sets the hue
     * @param hue The hue level in [0..2]
     */
    public void setHue(float hue) {
        this.hue = hue;
        setUniform(Hue, this.hue);
    }

    public float getGamma() {
        return gamma;
    }

    /**
     * Sets the gamma correction value
     * @param gamma Gamma value in [0..3]
     */
    public void setGamma(float gamma) {
        this.gamma = gamma;
        setUniform(Gamma, this.gamma);
    }
}
