package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class OldTvFilter extends PostProcessorFilter<OldTvFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Resolution("u_resolution", 2),
        Time("u_time", 0),
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

    private final Vector2 resolution = new Vector2();
    private float time = 0f;

    public OldTvFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/old-tv.frag")));
        rebind();
    }

    public void setTime(float time) {
        this.time = time;
        setParam(Param.Time, time);
    }

    @Override
    public void resize(int width, int height) {
        this.resolution.set(width, height);
        rebind();
    }

    @Override
    public void rebind () {
        setParams(Param.Texture0, u_texture0);
        setParams(Param.Resolution, resolution);
        setParams(Param.Time, time);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
