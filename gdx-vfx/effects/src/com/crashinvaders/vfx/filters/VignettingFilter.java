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
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class VignettingFilter extends VfxFilter<VignettingFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        TexLUT("u_texture1", 0),
        VignetteIntensity("u_vignetteIntensity", 0),
        VignetteX("u_vignetteX", 0),
        VignetteY("u_vignetteY", 0),
        Saturation("u_saturation", 0),
        SaturationMul("u_saturationMul", 0),
        LutIntensity("u_lutIntensity", 0),
        LutIndex1("u_lutIndex1", 0),
        LutIndex2("u_lutIndex2", 0),
        LutIndexOffset("u_lutIndexOffset", 0),
        LutStep("u_lutStep", 0),
        LutStepOffset("u_lutStepOffset", 0),
        CenterX("u_centerX", 0),
        CenterY("u_centerY", 0),
        ;

        final String mnemonic;
        final int elementSize;

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

    private float vignetteX = 0.8f;
    private float vignetteY = 0.25f;
    private float centerX = 0.5f;
    private float centerY = 0.5f;
    private float intensity = 1f;
    private float saturation = 0f;
    private float saturationMul = 0f;

    private boolean saturationEnabled;

    private boolean lutEnabled = false;
    private Texture lutTexture = null;
    private float lutIntensity = 1f;
    private int lutIndex1 = -1;
    private int lutIndex2 = -1;
    private float lutStep;
    private float lutStepOffset;
    private float lutIndexOffset = 0f;

    public VignettingFilter(boolean controlSaturation) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/vignetting.frag"),
                (controlSaturation ?
                        "#define CONTROL_SATURATION\n#define ENABLE_GRADIENT_MAPPING" :
                        "#define ENABLE_GRADIENT_MAPPING")));
        saturationEnabled = controlSaturation;
        rebind();
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        setParam(Param.VignetteIntensity, intensity);
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        if (saturationEnabled) {
            setParam(Param.Saturation, saturation);
        }
    }

    public void setSaturationMul(float saturationMul) {
        this.saturationMul = saturationMul;
        if (saturationEnabled) {
            setParam(Param.SaturationMul, saturationMul);
        }
    }

    public void setCoords(float x, float y) {
        this.vignetteX = x;
        this.vignetteY = y;
        setParams(Param.VignetteX, x);
        setParams(Param.VignetteY, y);
        endParams();
    }

    public void setVignetteX(float x) {
        this.vignetteX = x;
        setParam(Param.VignetteX, x);
    }

    public void setVignetteY(float vignetteY) {
        this.vignetteY = vignetteY;
        setParam(Param.VignetteY, vignetteY);
    }

    /**
     * Sets the texture with which gradient mapping will be performed.
     */
    public void setLut(Texture texture) {
        lutTexture = texture;
        lutEnabled = (lutTexture != null);

        if (lutEnabled) {
            lutStep = 1f / (float) texture.getHeight();
            lutStepOffset = lutStep / 2f; // center texel
            setParams(Param.TexLUT, u_texture1);
            setParams(Param.LutStep, lutStep);
            setParams(Param.LutStepOffset, lutStepOffset).endParams();
        }
    }

    public void setLutIntensity(float value) {
        lutIntensity = value;
        setParam(Param.LutIntensity, lutIntensity);
    }

    public void setLutIndex1(int value) {
        lutIndex1 = value;
        setParam(Param.LutIndex1, lutIndex1);
    }

    public void setLutIndex2(int value) {
        lutIndex2 = value;
        setParam(Param.LutIndex2, lutIndex2);
    }

    public void setLutIndexOffset(float value) {
        lutIndexOffset = value;
        setParam(Param.LutIndexOffset, lutIndexOffset);
    }

    /**
     * Specify the center, in normalized screen coordinates.
     */
    public void setCenter(float x, float y) {
        this.centerX = x;
        this.centerY = y;
        setParams(Param.CenterX, centerX);
        setParams(Param.CenterY, centerY);
        endParams();
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public int getLutIndex1() {
        return lutIndex1;
    }

    public int getLutIndex2() {
        return lutIndex2;
    }

    public float getLutIntensity() {
        return lutIntensity;
    }

    public Texture getLut() {
        return lutTexture;
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

    public boolean isGradientMappingEnabled() {
        return lutEnabled;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);

        setParams(Param.LutIndex1, lutIndex1);
        setParams(Param.LutIndex2, lutIndex2);
        setParams(Param.LutIndexOffset, lutIndexOffset);

        setParams(Param.TexLUT, u_texture1);
        setParams(Param.LutIntensity, lutIntensity);
        setParams(Param.LutStep, lutStep);
        setParams(Param.LutStepOffset, lutStepOffset);

        if (saturationEnabled) {
            setParams(Param.Saturation, saturation);
            setParams(Param.SaturationMul, saturationMul);
        }

        setParams(Param.VignetteIntensity, intensity);
        setParams(Param.VignetteX, vignetteX);
        setParams(Param.VignetteY, vignetteY);
        setParams(Param.CenterX, centerX);
        setParams(Param.CenterY, centerY);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
        if (lutEnabled) {
            lutTexture.bind(u_texture1);
        }
    }
}
