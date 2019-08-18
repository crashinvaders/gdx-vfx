package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class ChromaticAberrationFilter extends VfxFilter<ChromaticAberrationFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
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

    public ChromaticAberrationFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/chromatic-aberration.frag")));
        rebind();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind () {
        setParam(Param.Texture0, u_texture0);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
