package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.FisheyeDistortionFilter;

/**
 * Fisheye effect
 * @author tsagrista
 */
public final class FisheyeEffect extends VfxEffect {

    private final FisheyeDistortionFilter distort;

    public FisheyeEffect() {
        distort = new FisheyeDistortionFilter();
    }

    @Override
    public void dispose() {
        distort.dispose();
    }

    @Override
    public void rebind() {
        distort.rebind();
    }

    @Override
    public void resize(int width, int height) {
        distort.resize(width, height);
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        distort.setInput(src).setOutput(dst).render(mesh);
    }
}