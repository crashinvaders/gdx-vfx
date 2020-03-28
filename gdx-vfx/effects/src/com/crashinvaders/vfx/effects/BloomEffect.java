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
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.effects.GaussianBlurEffect.BlurType;
import com.crashinvaders.vfx.effects.util.CombineEffect;
import com.crashinvaders.vfx.effects.util.CopyEffect;
import com.crashinvaders.vfx.effects.util.GammaThresholdEffect;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class BloomEffect extends CompositeVfxEffect implements ChainVfxEffect {

    private final CopyEffect copy;
    private final GaussianBlurEffect blur;
    private final GammaThresholdEffect threshold;
    private final CombineEffect combine;

    private boolean blending = false;
    private int sfactor, dfactor;

    public BloomEffect() {
        this(new Settings(10, 0.85f, 1f, .85f, 1.1f, .85f));
    }

    public BloomEffect(Settings settings) {
        copy = register(new CopyEffect());
        blur = register(new GaussianBlurEffect());
        threshold = register(new GammaThresholdEffect(GammaThresholdEffect.Type.RGBA));
        combine = register(new CombineEffect());

        applySettings(settings);
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        // Preserve the input buffer data.
        VfxFrameBuffer origSrc = context.getBufferPool().obtain();
        copy.render(context, buffers.getSrcBuffer(), origSrc);

        boolean blendingWasEnabled = VfxGLUtils.isGLEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // High-pass filter
        // Only areas with pixels >= threshold are blit.
        threshold.render(context, buffers);
        buffers.swap();

        // Blur pass
        blur.render(context, buffers);
        buffers.swap();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            // TODO support for Gdx.gl.glBlendFuncSeparate(sfactor, dfactor, GL20.GL_ONE, GL20.GL_ONE );
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        // Mix original scene and blurred result).
        combine.render(context, origSrc, buffers.getSrcBuffer(), buffers.getDstBuffer());

        context.getBufferPool().free(origSrc);
    }

    public float getBaseIntensity() {
        return combine.getSource1Intensity();
    }

    public void setBaseIntensity(float intensity) {
        combine.setSource1Intensity(intensity);
    }

    public float getBaseSaturation() {
        return combine.getSource1Saturation();
    }

    public void setBaseSaturation(float saturation) {
        combine.setSource1Saturation(saturation);
    }

    public float getBloomIntensity() {
        return combine.getSource2Intensity();
    }

    public void setBloomIntensity(float intensity) {
        combine.setSource2Intensity(intensity);
    }

    public float getBloomSaturation() {
        return combine.getSource2Saturation();
    }

    public void setBloomSaturation(float saturation) {
        combine.setSource2Saturation(saturation);
    }

    public int getBlurPasses() {
        return blur.getPasses();
    }

    public void setBlurPasses(int passes) {
        blur.setPasses(passes);
    }

    public float getBlurAmount() {
        return blur.getAmount();
    }

    public void setBlurAmount(float amount) {
        blur.setAmount(amount);
    }

    public boolean isBlendingEnabled() {
        return blending;
    }

    public float getThreshold() {
        return threshold.getGamma();
    }

    public int getBlendingSourceFactor() {
        return sfactor;
    }

    public int getBlendingDestFactor() {
        return dfactor;
    }

    public void setThreshold(float gamma) {
        threshold.setGamma(gamma);
    }

    public void enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public BlurType getBlurType() {
        return blur.getType();
    }

    public void setBlurType(BlurType type) {
        blur.setType(type);
    }

    public void applySettings(Settings settings) {
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

    public static class Settings {

        public final BlurType blurType;
        public final int blurPasses;
        public final float blurAmount;
        public final float bloomThreshold;

        public final float bloomIntensity;
        public final float bloomSaturation;
        public final float baseIntensity;
        public final float baseSaturation;

        public Settings(int blurPasses, float bloomThreshold, float baseIntensity, float baseSaturation,
                        float bloomIntensity, float bloomSaturation) {
            this(BlurType.Gaussian5x5b, blurPasses, 0, bloomThreshold, baseIntensity, baseSaturation,
                    bloomIntensity,
                    bloomSaturation);
        }

        public Settings(BlurType blurType, int blurPasses, float blurAmount, float bloomThreshold,
                        float baseIntensity, float baseSaturation, float bloomIntensity, float bloomSaturation) {
            this.blurType = blurType;
            this.blurPasses = blurPasses;
            this.blurAmount = blurAmount;

            this.bloomThreshold = bloomThreshold;
            this.baseIntensity = baseIntensity;
            this.baseSaturation = baseSaturation;
            this.bloomIntensity = bloomIntensity;
            this.bloomSaturation = bloomSaturation;
        }

        public Settings(Settings other) {
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
