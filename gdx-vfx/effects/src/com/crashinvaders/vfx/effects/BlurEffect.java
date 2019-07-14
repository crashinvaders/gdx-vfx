package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.PostProcessorUtils;
import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.common.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.filters.*;

public class BlurEffect extends PostProcessorEffect {

    private final PingPongBuffer pingPongBuffer;
    private final CopyFilter copy;
    private final BlurFilter blur;

    private boolean blending = false;
    private int sfactor, dfactor;

    private int blurPasses;

    public BlurEffect() {
        this(1, BlurFilter.BlurType.Gaussian5x5);
    }

    public BlurEffect(int blurPasses, BlurFilter.BlurType blurType) {
        this.blurPasses = blurPasses;

        pingPongBuffer = new PingPongBuffer(Pixmap.Format.RGBA8888);

        copy = new CopyFilter();

        blur = new BlurFilter();
        blur.setPasses(blurPasses);
        blur.setType(blurType);
    }

    @Override
    public void dispose() {
        pingPongBuffer.dispose();
        blur.dispose();
        copy.dispose();
    }

    @Override
    public void resize(int width, int height) {
        pingPongBuffer.resize(width, height);
        blur.resize(width, height);
        copy.resize(width, height);
    }

    @Override
    public void rebind() {
        pingPongBuffer.rebind();
        blur.rebind();
        copy.rebind();
    }

    @Override
    public void render(FboWrapper src, FboWrapper dest) {
        if (blurPasses < 1) {
            // Do not apply blur filter.
            copy.setInput(src).setOutput(dest).render();
            return;
        }

        boolean blendingWasEnabled = PostProcessorUtils.isGlEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pingPongBuffer.begin();
        copy.setInput(src).setOutput(pingPongBuffer.getSourceBuffer()).render();
        blur.render(pingPongBuffer);
        pingPongBuffer.end();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            // TODO support for Gdx.gl.glBlendFuncSeparate(sfactor, dfactor, GL20.GL_ONE, GL20.GL_ONE );
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        copy.setInput(pingPongBuffer.getResultTexture())
                .setOutput(dest)
                .render();
    }

    public BlurEffect enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
        return this;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public BlurEffect setBlurPasses(int blurPasses) {
        this.blurPasses = blurPasses;
        blur.setPasses(blurPasses);
        return this;
    }

    public int getBlurPasses() {
        return blurPasses;
    }
}
