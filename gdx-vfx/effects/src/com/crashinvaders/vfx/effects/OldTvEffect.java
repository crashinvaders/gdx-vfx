package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.OldTvFilter;
import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;

public class OldTvEffect extends VfxEffect implements UpdateableEffect {

    private final OldTvFilter oldTvFilter;

    private float time;

    public OldTvEffect() {
        oldTvFilter = new OldTvFilter();
    }

    @Override
    public void resize(int width, int height) {
        oldTvFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        oldTvFilter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dest) {
        oldTvFilter.setInput(src).setOutput(dest).render(mesh);
    }

    @Override
    public void dispose() {
        oldTvFilter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time += delta;
        oldTvFilter.setTime(time);
    }
}
