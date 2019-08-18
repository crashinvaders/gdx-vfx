/*******************************************************************************
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
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxFilter;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Fast approximate anti-aliasing filter.
 * @author Toni Sagrista */
public final class FxaaFilter extends VfxFilter<FxaaFilter> {

	private final Vector2 viewportInverse = new Vector2();
	private float fxaaReduceMin;
	private float fxaaReduceMul;
	private float fxaaSpanMax;

	public enum Param implements Parameter {
		// @formatter:off
		Texture("u_texture0", 0),
		ViewportInverse("u_viewportInverse", 2),
		FxaaReduceMin("u_fxaaReduceMin", 0),
		FxaaReduceMul("u_fxaaReduceMul", 0),
		FxaaSpanMax("u_fxaaSpanMax", 0);
		// @formatter:on

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

	public FxaaFilter (float fxaaReduceMin, float fxaaReduceMul, float fxaaSpanMax, boolean supportAlpha) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/fxaa.frag"),
				supportAlpha ? "#define SUPPORT_ALPHA" : ""));
		this.fxaaReduceMin = fxaaReduceMin;
		this.fxaaReduceMul = fxaaReduceMul;
		this.fxaaSpanMax = fxaaSpanMax;
		rebind();
	}

	public void setViewportSize (float width, float height) {
		this.viewportInverse.set(1f / width, 1f / height);
		setParam(Param.ViewportInverse, this.viewportInverse);
	}

	/** Sets the parameter. The default value is 1/128.
	 * @param value */
	public void setFxaaReduceMin (float value) {
		this.fxaaReduceMin = value;
		setParam(Param.FxaaReduceMin, this.fxaaReduceMin);
	}

	/** Sets the parameter. The default value is 1/8.
	 * @param value */
	public void setFxaaReduceMul (float value) {
		this.fxaaReduceMul = value;
		setParam(Param.FxaaReduceMul, this.fxaaReduceMul);
	}

	/** Sets the parameter. The default value is 8;
	 * @param value */
	public void setFxaaSpanMax (float value) {
		this.fxaaSpanMax = value;
		setParam(Param.FxaaSpanMax, this.fxaaSpanMax);
	}

	public Vector2 getViewportSize () {
		return viewportInverse;
	}

    @Override
    public void resize(int width, int height) {
		this.viewportInverse.set(1f / width, 1f / height);
		setParam(Param.ViewportInverse, this.viewportInverse);
    }

    @Override
	public void rebind () {
		// reimplement super to batch every parameter
		setParams(Param.Texture, u_texture0);
		setParams(Param.ViewportInverse, viewportInverse);
		setParams(Param.FxaaReduceMin, fxaaReduceMin);
		setParams(Param.FxaaReduceMul, fxaaReduceMul);
		setParams(Param.FxaaSpanMax, fxaaSpanMax);
		endParams();
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);
	}
}
