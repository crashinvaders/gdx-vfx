package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.FilmGrainFilter;

public class FilmGrainEffect extends PostProcessorEffect implements UpdateableEffect {

    private final FilmGrainFilter filmGrainFilter;

    private float time = 0f;

    public FilmGrainEffect() {
        filmGrainFilter = new FilmGrainFilter();
    }

    @Override
    public void resize(int width, int height) {
        filmGrainFilter.resize(width, height);
    }

    @Override
    public void rebind() {
        filmGrainFilter.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, FboWrapper src, FboWrapper dst) {
        filmGrainFilter.setInput(src).setOutput(dst).render(mesh);
    }

    @Override
    public void dispose() {
        filmGrainFilter.dispose();
    }

    @Override
    public void update(float delta) {
        this.time = (this.time + delta) % 1f;
        filmGrainFilter.setSeed(this.time);
    }
}
