/*******************************************************************************
 * Copyright 2012 bmanuel
 * Copyright 2019 metaphore
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class RadialBlurFilter extends VfxFilterOld<RadialBlurFilter> {

	public enum Param implements Parameter {
		Texture("u_texture0", 0), 
		BlurDiv("u_blurDiv", 0),
		OffsetX("u_offsetX", 0),
		OffsetY("u_offsetY", 0),
		Zoom("u_zoom", 0), ;

		final String mnemonic;
		final int elementSize;

		Param(String mnemonic, int arrayElementSize) {
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

	private final int passes;
	private float strength = 0.2f;
	private float originX = 0.5f;
	private float originY = 0.5f;
	private float zoom = 1f;

	public RadialBlurFilter(int passes) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/radial-blur.vert"),
				Gdx.files.classpath("shaders/radial-blur.frag"),
				"#define PASSES " + passes));
		this.passes = passes;
		rebind();
	}

	public float getOriginX () {
		return originX;
	}

	public float getOriginY () {
		return originY;
	}

	/**
	 * Specify the zoom origin in {@link Align} bits.
	 * @see Align
	 */
	public void setOrigin(int align) {
		final float originX;
		final float originY;
		if ((align & Align.left) != 0) {
			originX = 0f;
		} else if ((align & Align.right) != 0) {
			originX = 1f;
		} else {
			originX = 0.5f;
		}
		if ((align & Align.bottom) != 0) {
			originY = 0f;
		} else if ((align & Align.top) != 0) {
			originY = 1f;
		} else {
			originY = 0.5f;
		}
		setOrigin(originX, originY);
	}

	/**
	 * Specify the zoom origin in normalized screen coordinates.
	 * @param originX horizontal origin [0..1].
	 * @param originY vertical origin [0..1].
	 */
	public void setOrigin (float originX, float originY) {
		this.originX = originX;
		this.originY = originY;
		setParams(ZoomFilter.Param.OffsetX, this.originX);
		setParams(ZoomFilter.Param.OffsetY, this.originY);
		endParams();
	}

	public float getStrength () {
		return strength;
	}

	public void setStrength (float strength) {
		this.strength = strength;
		setParam(Param.BlurDiv, strength / (float) passes);
	}

	public float getZoom () {
		return zoom;
	}

	public void setZoom (float zoom) {
		this.zoom = zoom;
		setParam(Param.Zoom, this.zoom);
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
		setParams(Param.BlurDiv, this.strength / (float) passes);
		setParams(Param.OffsetX, originX);
		setParams(Param.OffsetY, originY);
		setParams(Param.Zoom, zoom);
		endParams();
	}
}
