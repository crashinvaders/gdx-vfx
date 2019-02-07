/*******************************************************************************
 * Copyright 2012 bmanuel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.filters;

import com.crashinvaders.vfx.PostProcessorFilter;
import com.crashinvaders.vfx.utils.ShaderLoader;

public final class RadialBlur extends PostProcessorFilter<RadialBlur> {

	private int blurLen;
	private float strength = 0.2f;
	private float x = 0.5f;
	private float y = 0.5f;
	private float zoom = 1f;

	public enum Quality {
		VeryHigh(16), High(8), Normal(5), Medium(4), Low(2);

		final int length;

		private Quality (int value) {
			this.length = value;
		}
	}

	public enum Param implements Parameter {
		// @off
		Texture("u_texture0", 0), 
		BlurDiv("blur_div", 0), 
		OffsetX("offset_x", 0), 
		OffsetY("offset_y", 0),
		// OneOnBlurLen( "one_on_blurlen", 0 ),
		Zoom("zoom", 0), ;
		// @on

		private String mnemonic;
		private int elementSize;

		private Param (String mnemonic, int arrayElementSize) {
			this.mnemonic = mnemonic;
			this.elementSize = arrayElementSize;
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

	public RadialBlur (Quality quality) {
		super(ShaderLoader.fromFile("radial-blur", "radial-blur",
				"#define BLUR_LENGTH " + quality.length +
				"\n#define ONE_ON_BLUR_LENGTH " + 1f / (float)quality.length));
		this.blurLen = quality.length;
		rebind();
	}

	public RadialBlur () {
		this(Quality.Low);
	}

	public void setOrigin (float x, float y) {
		this.x = x;
		this.y = y;
		setParams(Param.OffsetX, x);
		setParams(Param.OffsetY, y);
		endParams();
	}

	public void setStrength (float strength) {
		this.strength = strength;
		setParam(Param.BlurDiv, strength / (float) blurLen);
	}

	public void setZoom (float zoom) {
		this.zoom = zoom;
		setParam(Param.Zoom, this.zoom);
	}

	public float getZoom () {
		return zoom;
	}

	public float getOriginX () {
		return x;
	}

	public float getOriginY () {
		return y;
	}

	public float getStrength () {
		return strength;
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);
	}

    @Override
    public void resize(int width, int height) {
		// Do nothing.
    }

    @Override
	public void rebind () {
		setParams(Param.Texture, u_texture0);
		setParams(Param.BlurDiv, this.strength / (float) blurLen);

		// being explicit (could call setOrigin that will call endParams)
		setParams(Param.OffsetX, x);
		setParams(Param.OffsetY, y);

		setParams(Param.Zoom, zoom);

		endParams();
	}
}
