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
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class VignettingEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String TEXTURE0 = "u_texture0";
    private static final String VIGNETTE_INTENSITY = "u_vignetteIntensity";
    private static final String VIGNETTE_X = "u_vignetteX";
    private static final String VIGNETTE_Y = "u_vignetteY";
    private static final String CENTER_X = "u_centerX";
    private static final String CENTER_Y = "u_centerY";
    private static final String SATURATION = "u_saturation";
    private static final String SATURATION_MUL = "u_saturationMul";


    private float vignetteX = 0.8f;
    private float vignetteY = 0.25f;
    private float centerX = 0.5f;
    private float centerY = 0.5f;
    private float intensity = 1f;

    private final boolean saturationEnabled;
    private float saturation = 0f;
    private float saturationMul = 0f;

    public VignettingEffect(boolean controlSaturation) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/vignetting.frag"),
                (controlSaturation ? "#define CONTROL_SATURATION" : "")));
        this.saturationEnabled = controlSaturation;
        rebind();
    }

    @Override
    public void rebind() {
        program.begin();
        program.setUniformi(TEXTURE0, TEXTURE_HANDLE0);

        if (saturationEnabled) {
            program.setUniformf(SATURATION, saturation);
            program.setUniformf(SATURATION_MUL, saturationMul);
        }

        program.setUniformf(VIGNETTE_INTENSITY, intensity);
        program.setUniformf(VIGNETTE_X, vignetteX);
        program.setUniformf(VIGNETTE_Y, vignetteY);
        program.setUniformf(CENTER_X, centerX);
        program.setUniformf(CENTER_Y, centerY);
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

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        setUniform(VIGNETTE_INTENSITY, intensity);
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        if (saturationEnabled) {
            setUniform(SATURATION, saturation);
        }
    }

    public void setSaturationMul(float saturationMul) {
        this.saturationMul = saturationMul;
        if (saturationEnabled) {
            setUniform(SATURATION_MUL, saturationMul);
        }
    }

    public void setCoords(float x, float y) {
        this.vignetteX = x;
        this.vignetteY = y;
        program.begin();
        program.setUniformf(VIGNETTE_X, x);
        program.setUniformf(VIGNETTE_Y, y);
        program.end();
    }

    public void setVignetteX(float x) {
        this.vignetteX = x;
        setUniform(VIGNETTE_X, x);
    }

    public void setVignetteY(float vignetteY) {
        this.vignetteY = vignetteY;
        setUniform(VIGNETTE_Y, vignetteY);
    }

    /** Specify the center, in normalized screen coordinates. */
    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;

        program.begin();
        program.setUniformf(CENTER_X, centerX);
        program.setUniformf(CENTER_Y, centerY);
        program.end();
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public float getVignetteX() {
        return vignetteX;
    }

    public float getVignetteY() {
        return vignetteY;
    }

    public float getIntensity() {
        return intensity;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getSaturationMul() {
        return saturationMul;
    }

    public boolean isSaturationControlEnabled() {
        return saturationEnabled;
    }
}
