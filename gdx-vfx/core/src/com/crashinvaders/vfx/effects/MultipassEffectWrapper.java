package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;

public class MultipassEffectWrapper extends AbstractVfxEffect implements ChainVfxEffect {

    private final ChainVfxEffect effect;
    private int passes = 1;

    public MultipassEffectWrapper(ChainVfxEffect effect) {
        this.effect = effect;
    }

    @Override
    public void resize(int width, int height) {
        effect.resize(width, height);
    }

    @Override
    public void update(float delta) {
        effect.update(delta);
    }

    @Override
    public void rebind() {
        effect.rebind();
    }

    @Override
    public void dispose() {
        effect.dispose();
    }

    @Override
    public void render(VfxRenderContext context, PingPongBuffer pingPongBuffer) {
        // Simply swap buffers to simulate render skip.
        if (passes == 0) {
            pingPongBuffer.swap();
            return;
        }

        final int finalPasses = this.passes;
        for (int i = 0; i < finalPasses; i++) {
            effect.render(context, pingPongBuffer);
            if (i < finalPasses - 1) {
                pingPongBuffer.swap();
            }
        }
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        if (passes < 0) {
            throw new IllegalArgumentException("Passes value cannot be a negative number.");
        }
        this.passes = passes;
    }
}
