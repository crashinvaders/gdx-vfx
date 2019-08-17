package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.LevelsFilter;

/** Implements brightness, contrast, hue and saturation levels
 * @author tsagrista */
public final class LevelsEffect extends PostProcessorEffect {
    private final LevelsFilter filter;

    /** Creates the effect */
    public LevelsEffect() {
        filter = new LevelsFilter();
    }

    @Override
    public void dispose() {
        filter.dispose();
    }

    @Override
    public void resize(int width, int height) {
        filter.resize(width, height);
    }

    @Override
    public void rebind() {
        filter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, FboWrapper src, FboWrapper dst) {
        filter.setInput(src).setOutput(dst).render(mesh);
    }

    /** Set the brightness
     * @param value The brightness value in [-1..1] */
    public LevelsEffect setBrightness(float value) {
        filter.setBrightness(value);
        return this;
    }

    /** Set the saturation
     * @param value The saturation value in [0..2] */
    public LevelsEffect setSaturation(float value) {
        filter.setSaturation(value);
        return this;
    }

    /** Set the hue
     * @param value The hue value in [0..2] */
    public LevelsEffect setHue(float value) {
        filter.setHue(value);
        return this;
    }

    /** Set the contrast
     * @param value The contrast value in [0..2] */
    public LevelsEffect setContrast(float value) {
        filter.setContrast(value);
        return this;
    }

    /** Sets the gamma correction value
     * @param value The gamma value in [0..3] */
    public LevelsEffect setGamma(float value) {
        filter.setGamma(value);
        return this;
    }

    public float getBrightness() {
        return filter.getBrightness();
    }

    public float getContrast() {
        return filter.getContrast();
    }

    public float getSaturation() {
        return filter.getSaturation();
    }

    public float getHue() {
        return filter.getHue();
    }

    public float getGamma() {
        return filter.getGamma();
    }
}
