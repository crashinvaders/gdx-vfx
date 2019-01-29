package com.crashinvaders.vfx.effects;

import com.crashinvaders.common.framebuffer.FboWrapper;
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
    public void render(FboWrapper src, FboWrapper dest) {
        caFilter.setInput(src).setOutput(dest).render();
    }

    @Override
    public void dispose() {
        caFilter.dispose();
    }
}
