package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.filters.NoiseFilter;

public class NoiseEffect extends VfxEffect implements UpdateableEffect {

    private final NoiseFilter filter;

    private float time;

    public NoiseEffect() {
        this(0.35f, 2f);
    }

    public NoiseEffect(float amount, float speed) {
        filter = new NoiseFilter(amount, speed);
    }

    public float getAmount() {
        return filter.getAmount();
    }

    public void setAmount(float amount) {
        filter.setAmount(amount);
    }

    public float getSpeed() {
        return filter.getSpeed();
    }

    public void setSpeed(float speed) {
        filter.setSpeed(speed);
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
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        filter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        filter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time += delta;
        filter.setTime(time);
    }
}
