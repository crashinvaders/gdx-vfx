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

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.vfx.framebuffer.RegularPingPongBuffer;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.crashinvaders.vfx.filters.GaussianBlurFilter;
import com.crashinvaders.vfx.filters.GaussianBlurFilter.BlurType;
import com.crashinvaders.vfx.filters.CombineFilterOld;
import com.crashinvaders.vfx.filters.ThresholdFilterOld;

public final class BloomEffect extends VfxEffectOld {

    private final PingPongBuffer pingPongBuffer;

    private final GaussianBlurFilter blur;
    private final ThresholdFilterOld threshold;
    private final CombineFilterOld combine;

    private Settings settings;

    private boolean blending = false;
    private int sfactor, dfactor;

    public BloomEffect(Pixmap.Format bufferFormat) {
        this(bufferFormat, new Settings("default", 10, 0.85f, 1f, .85f, 1.1f, .85f));
    }

    public BloomEffect(Pixmap.Format bufferFormat, Settings settings) {
        pingPongBuffer = new RegularPingPongBuffer(bufferFormat);

        blur = new GaussianBlurFilter();
        threshold = new ThresholdFilterOld();
        combine = new CombineFilterOld();

        setSettings(settings);
    }

    @Override
    public void resize(int width, int height) {
        pingPongBuffer.resize(width, height);

        blur.resize(width, height);
        threshold.resize(width, height);
        combine.resize(width, height);
    }

    @Override
    public void dispose() {
        combine.dispose();
        threshold.dispose();
        blur.dispose();
        pingPongBuffer.dispose();
    }

    public void setBaseIntensity(float intensity) {
        combine.setSource1Intensity(intensity);
    }

    public void setBaseSaturation(float saturation) {
        combine.setSource1Saturation(saturation);
    }

    public void setBloomIntensity(float intensity) {
        combine.setSource2Intensity(intensity);
    }

    public void setBloomSaturation(float saturation) {
        combine.setSource2Saturation(saturation);
    }

    public void setThreshold(float gamma) {
        threshold.setTreshold(gamma);
    }

    public void enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public void setBlurType(BlurType type) {
        blur.setType(type);
    }

    public void setSettings(Settings settings) {
        this.settings = settings;

        // Setup threshold filter
        setThreshold(settings.bloomThreshold);

        // Setup combine filter
        setBaseIntensity(settings.baseIntensity);
        setBaseSaturation(settings.baseSaturation);
        setBloomIntensity(settings.bloomIntensity);
        setBloomSaturation(settings.bloomSaturation);

        // Setup blur filter
        setBlurPasses(settings.blurPasses);
        setBlurAmount(settings.blurAmount);
        setBlurType(settings.blurType);
    }

    public void setBlurPasses(int passes) {
        blur.setPasses(passes);
    }

    public void setBlurAmount(float amount) {
        blur.setAmount(amount);
    }

    public float getThreshold() {
        return threshold.getThreshold();
    }

    public float getBaseIntensity() {
        return combine.getSource1Intensity();
    }

    public float getBaseSaturation() {
        return combine.getSource1Saturation();
    }

    public float getBloomIntensity() {
        return combine.getSource2Intensity();
    }

    public float getBloomSaturation() {
        return combine.getSource2Saturation();
    }

    public boolean isBlendingEnabled() {
        return blending;
    }

    public int getBlendingSourceFactor() {
        return sfactor;
    }

    public int getBlendingDestFactor() {
        return dfactor;
    }

    public BlurType getBlurType() {
        return blur.getType();
    }

    public Settings getSettings() {
        return settings;
    }

    public int getBlurPasses() {
        return blur.getPasses();
    }

    public float getBlurAmount() {
        return blur.getAmount();
    }

    @Override
    public void render(ViewportQuadMesh mesh, final VfxFrameBuffer src, final VfxFrameBuffer dst) {
        Texture texSrc = src.getFbo().getColorBufferTexture();

        boolean blendingWasEnabled = VfxGLUtils.isGLEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pingPongBuffer.begin();
        {
            // Threshold / high-pass filter
            // Only areas with pixels >= threshold are blit to smaller FBO
            threshold.setInput(texSrc).setOutput(pingPongBuffer.getDstBuffer()).render(mesh);
            pingPongBuffer.swap();

            // Blur pass
            blur.render(mesh, pingPongBuffer);
        }
        pingPongBuffer.end();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            // TODO support for Gdx.gl.glBlendFuncSeparate(sfactor, dfactor, GL20.GL_ONE, GL20.GL_ONE );
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        // Mix original scene and blurred threshold, modulate via set(Base|BloomEffect)(Saturation|Intensity)
        combine.setInput(texSrc, pingPongBuffer.getDstTexture())
                .setOutput(dst)
                .render(mesh);
    }

    @Override
    public void rebind() {
        blur.rebind();
        threshold.rebind();
        combine.rebind();
        pingPongBuffer.rebind();
    }

    public static class Settings {
        public final String name;

        public final BlurType blurType;
        public final int blurPasses;
        public final float blurAmount;
        public final float bloomThreshold;

        public final float bloomIntensity;
        public final float bloomSaturation;
        public final float baseIntensity;
        public final float baseSaturation;

        public Settings(String name, BlurType blurType, int blurPasses, float blurAmount, float bloomThreshold,
                        float baseIntensity, float baseSaturation, float bloomIntensity, float bloomSaturation) {
            this.name = name;
            this.blurType = blurType;
            this.blurPasses = blurPasses;
            this.blurAmount = blurAmount;

            this.bloomThreshold = bloomThreshold;
            this.baseIntensity = baseIntensity;
            this.baseSaturation = baseSaturation;
            this.bloomIntensity = bloomIntensity;
            this.bloomSaturation = bloomSaturation;
        }

        public Settings(String name, int blurPasses, float bloomThreshold, float baseIntensity, float baseSaturation,
                        float bloomIntensity, float bloomSaturation) {
            this(name, BlurType.Gaussian5x5b, blurPasses, 0, bloomThreshold, baseIntensity, baseSaturation,
                    bloomIntensity,
                    bloomSaturation);
        }

        public Settings(Settings other) {
            this.name = other.name;
            this.blurType = other.blurType;
            this.blurPasses = other.blurPasses;
            this.blurAmount = other.blurAmount;

            this.bloomThreshold = other.bloomThreshold;
            this.baseIntensity = other.baseIntensity;
            this.baseSaturation = other.baseSaturation;
            this.bloomIntensity = other.bloomIntensity;
            this.bloomSaturation = other.bloomSaturation;
        }
    }
}
