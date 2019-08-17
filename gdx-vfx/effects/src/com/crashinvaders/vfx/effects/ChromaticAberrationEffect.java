package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.ChromaticAberrationFilter;

public class ChromaticAberrationEffect extends PostProcessorEffect {

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
    public void render(ScreenQuadMesh mesh, FboWrapper src, FboWrapper dst) {
        caFilter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        caFilter.dispose();
    }
}
