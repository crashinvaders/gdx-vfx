package com.crashinvaders.vfx.effects;

import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
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
    public void render(FboWrapper src, FboWrapper dest) {
        filmGrainFilter.setInput(src).setOutput(dest).render();
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
