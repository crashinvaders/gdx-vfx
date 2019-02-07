package com.crashinvaders.vfx.filters;

import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.utils.ShaderLoader;

/**
 * Fisheye distortion filter
 * @author tsagrista
 */
public class FisheyeDistortionFilter extends PostProcessorFilter<FisheyeDistortionFilter> {

    public enum Param implements Parameter {
        // @formatter:off
        Texture0("u_texture0", 0);
        // @formatter:on

        private final String mnemonic;
        private int elementSize;

        private Param(String m, int elementSize) {
            this.mnemonic = m;
            this.elementSize = elementSize;
        }

        @Override
        public String mnemonic() {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize() {
            return this.elementSize;
        }
    }

    public FisheyeDistortionFilter() {
        super(ShaderLoader.fromFile("screenspace", "fisheye"));
        rebind();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }

    @Override
    public void rebind() {
        setParams(Param.Texture0, u_texture0);
        endParams();
    }
    @Override
    public void resize(int width, int height) {
        // Do nothing.
    }
}
