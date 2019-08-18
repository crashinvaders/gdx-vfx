package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffect;
import com.crashinvaders.vfx.filters.ChromaticAberrationFilter;

public class ChromaticAberrationEffect extends VfxEffect {

    private final ChromaticAberrationFilter caFilter;

    public ChromaticAberrationEffect() {
        caFilter = new ChromaticAberrationFilter();
    }

    @Override
    public void resize(int width, int height) {
        caFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        caFilter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        caFilter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        caFilter.dispose();
    }
}
