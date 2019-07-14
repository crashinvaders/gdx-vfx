package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.utils.ShaderLoader;

public class CrtFilter extends PostProcessorFilter<CrtFilter> {
    private static final Vector2 tmpVec = new Vector2();

    public enum Param implements Parameter {
        Texture0("u_texture0", 0),
        Resolution("u_resolution", 2),
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

    public enum LineStyle {
        CROSSLINE_HARD,
        VERTICAL_HARD,
        HORIZONTAL_HARD,
        VERTICAL_SMOOTH,
        HORIZONTAL_SMOOTH,
    }

    /** Shader resolution parameter source. */
    public enum SizeSource {
        /** Resolution will be defined by the application internal viewport. */
        VIEWPORT,
        /** Resolution will be defined by the application window size. */
        SCREEN,
    }

    private final Vector2 viewportSize = new Vector2();
    private SizeSource sizeSource = SizeSource.VIEWPORT;

    public CrtFilter() {
        this(LineStyle.HORIZONTAL_HARD, 1.3f, 0.5f);
    }

    /** Brightness is a value between [0..2] (default is 1.0). */
    public CrtFilter(LineStyle lineStyle, float brightnessMin, float brightnessMax) {
        super(ShaderLoader.fromFile("screenspace", "crt",
                "#define SL_BRIGHTNESS_MIN " + brightnessMin + "\n" +
                "#define SL_BRIGHTNESS_MAX " + brightnessMax + "\n" +
                "#define LINE_TYPE " + lineStyle.ordinal()));
        rebind();
    }

    public SizeSource getSizeSource() {
        return sizeSource;
    }

    /** Set shader resolution parameter source.
     * @see SizeSource */
    public void setSizeSource(SizeSource sizeSource) {
        if (sizeSource == null) {
            throw new IllegalArgumentException("Size source cannot be null.");
        }
        if (this.sizeSource == sizeSource) {
            return;
        }
        this.sizeSource = sizeSource;
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        this.viewportSize.set(width, height);
        rebind();
    }

    @Override
    public void rebind () {
        setParam(Param.Texture0, u_texture0);
        switch (sizeSource) {
            case VIEWPORT:
                setParam(Param.Resolution, viewportSize);
                break;
            case SCREEN:
                setParam(Param.Resolution, tmpVec.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                break;
        }
        endParams();
    }

    @Override
    protected void onBeforeRender () {
        inputTexture.bind(u_texture0);
    }
}
