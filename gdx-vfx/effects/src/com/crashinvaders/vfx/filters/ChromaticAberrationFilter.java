package com.crashinvaders.vfx.filters;

import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.utils.ShaderLoader;

public class ChromaticAberrationFilter extends PostProcessorFilter<ChromaticAberrationFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
//        Resolution("u_resolution", 2),
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

    public ChromaticAberrationFilter() {
        super(ShaderLoader.fromFile("screenspace", "chromatic-aberration"));
        rebind();
    }

    @Override
    public void resize(int width, int height) {
//        this.resolution.set(width, height);
//        rebind();
    }

    @Override
    public void rebind () {
        setParam(Param.Texture0, u_texture0);
//        setParam(Param.Resolution, resolution);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
