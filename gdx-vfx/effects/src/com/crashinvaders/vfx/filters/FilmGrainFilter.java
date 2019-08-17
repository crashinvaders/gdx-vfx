package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class FilmGrainFilter extends PostProcessorFilter<FilmGrainFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
//        Resolution("u_resolution", 2),
        Seed("u_seed", 0),
        ;

        private final String mnemonic;
        private int elementSize;

        Param (String m, int elementSize) {
            this.mnemonic = m;
            this.elementSize = elementSize;
        }

        @Override
        public String mnemonic () {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize () {
            return this.elementSize;
        }
    }

//    private final Vector2 resolution = new Vector2();
    private float seed = 0f;

    public FilmGrainFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/film-grain.frag")));
        rebind();
    }

    public void setSeed(float seed) {
        this.seed = seed;
        rebind();
    }

    @Override
    public void resize(int width, int height) {
//        resolution.set(width, height);
//        rebind();
    }

    @Override
    public void rebind () {
        setParam(Param.Texture0, u_texture0);
//        setParam(Param.Resolution, resolution);
        setParam(Param.Seed, seed);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
