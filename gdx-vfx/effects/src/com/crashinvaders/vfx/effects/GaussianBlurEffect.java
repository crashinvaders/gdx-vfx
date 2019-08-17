package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;
import com.crashinvaders.vfx.gl.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.crashinvaders.vfx.filters.CopyFilter;
import com.crashinvaders.vfx.filters.GaussianBlurFilter;

public class GaussianBlurEffect extends PostProcessorEffect {

    private final PingPongBuffer pingPongBuffer;
    private final CopyFilter copy;
    private final GaussianBlurFilter blur;

    private boolean blending = false;
    private int sfactor, dfactor;

    public GaussianBlurEffect() {
        this(1, GaussianBlurFilter.BlurType.Gaussian5x5);
    }

    public GaussianBlurEffect(int blurPasses, GaussianBlurFilter.BlurType blurType) {
        pingPongBuffer = new PingPongBuffer(Pixmap.Format.RGBA8888);

        copy = new CopyFilter();

        blur = new GaussianBlurFilter();
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
    public void render(ScreenQuadMesh mesh, FboWrapper src, FboWrapper dst) {
        if (blur.getPasses() < 1) {
            // Do not apply blur filter.
            copy.setInput(src).setOutput(dst).render(mesh);
            return;
        }

        boolean blendingWasEnabled = VfxGLUtils.isGLEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pingPongBuffer.begin();
        copy.setInput(src).setOutput(pingPongBuffer.getDstBuffer()).render(mesh);
        pingPongBuffer.swap();
        blur.render(mesh, pingPongBuffer);
        pingPongBuffer.end();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            // TODO support for Gdx.gl.glBlendFuncSeparate(sfactor, dfactor, GL20.GL_ONE, GL20.GL_ONE );
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        copy.setInput(pingPongBuffer.getDstTexture())
                .setOutput(dst)
                .render(mesh);
    }

    public GaussianBlurEffect enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
        return this;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public GaussianBlurEffect setBlurPasses(int blurPasses) {
        blur.setPasses(blurPasses);
        return this;
    }

    public int getBlurPasses() {
        return blur.getPasses();
    }
}
