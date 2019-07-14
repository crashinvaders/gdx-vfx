
package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.utils.ShaderLoader;

/** Motion blur filter that draws the last frame (motion filter included) with a lower opacity.
 * @author Toni Sagrista */
public class MotionBlurFilter extends PostProcessorFilter<MotionBlurFilter> {

	private float blurOpacity = 0.5f;
	private Texture lastFrameTex;

	/** Defines which function will be used to mix the two frames to produce motion blur effect. */
	public enum BlurFunction {
		MAX("motionblur-max"),
		MIX("motionblur-mix");

		final String fragmentShaderName;

		BlurFunction(String fragmentShaderName) {
			this.fragmentShaderName = fragmentShaderName;
		}
	}

	public enum Param implements Parameter {
        Texture("u_texture0", 0),
        LastFrame("u_texture1", 0),
        BlurOpacity("u_blurOpacity", 0);

        private String mnemonic;
        private int elementSize;

        Param(String mnemonic, int arrayElementSize) {
            this.mnemonic = mnemonic;
            this.elementSize = arrayElementSize;
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

	public MotionBlurFilter(BlurFunction blurFunction) {
		super(ShaderLoader.fromFile("screenspace", blurFunction.fragmentShaderName));
		rebind();
	}

	public void setBlurOpacity (float blurOpacity) {
		this.blurOpacity = blurOpacity;
		setParam(Param.BlurOpacity, this.blurOpacity);
	}

	public void setLastFrameTexture (Texture tex) {
		this.lastFrameTex = tex;
		if (lastFrameTex != null) {
			setParam(Param.LastFrame, u_texture1);
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void rebind () {
		setParams(Param.Texture, u_texture0);
		if (lastFrameTex != null) {
			setParams(Param.LastFrame, u_texture1);
		}
		setParams(Param.BlurOpacity, this.blurOpacity);
		endParams();
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);
		if (lastFrameTex != null) {
			lastFrameTex.bind(u_texture1);
		}
	}
}