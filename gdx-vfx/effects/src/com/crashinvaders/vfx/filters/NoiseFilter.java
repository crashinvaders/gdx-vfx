package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class NoiseFilter extends PostProcessorFilter<NoiseFilter> {

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
//        Resolution("u_resolution", 2),
        Amount("u_amount", 0),
        Speed("u_speed", 0),
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

//    private final Vector2 resolution = new Vector2();
    private float amount;
    private float speed;
    private float time = 0f;

    public NoiseFilter(float amount, float speed) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/noise.frag")));
        this.amount = amount;
        this.speed = speed;
        rebind();
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
        setParam(Param.Time, time);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void resize(int width, int height) {
//        this.resolution.set(width, height);
//        rebind();
    }

    @Override
    public void rebind () {
        setParams(Param.Texture0, u_texture0);
//        setParams(Param.Resolution, resolution);
        setParams(Param.Amount, amount);
        setParams(Param.Speed, speed);
        setParams(Param.Time, time);
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}

