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
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Fast approximate anti-aliasing filter.
 * @author Toni Sagrista */
public final class FxaaFilterOld extends VfxFilterOld<FxaaFilterOld> {

	private final Vector2 viewportInverse = new Vector2();
	private float fxaaReduceMin;
	private float fxaaReduceMul;
	private float fxaaSpanMax;

	public enum Param implements Parameter {
		Texture("u_texture0", 0),
		ViewportInverse("u_viewportInverse", 2),
		FxaaReduceMin("u_fxaaReduceMin", 0),
		FxaaReduceMul("u_fxaaReduceMul", 0),
		FxaaSpanMax("u_fxaaSpanMax", 0);

		private String mnemonic;
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

	public FxaaFilterOld(float fxaaReduceMin, float fxaaReduceMul, float fxaaSpanMax, boolean supportAlpha) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/fxaa.frag"),
				supportAlpha ? "#define SUPPORT_ALPHA" : ""));
		this.fxaaReduceMin = fxaaReduceMin;
		this.fxaaReduceMul = fxaaReduceMul;
		this.fxaaSpanMax = fxaaSpanMax;
		rebind();
	}

	/** Sets the parameter. The default value is 1/128.
	 * @param value */
	public void setReduceMin (float value) {
		this.fxaaReduceMin = value;
		setParam(Param.FxaaReduceMin, this.fxaaReduceMin);
	}

	/** Sets the parameter. The default value is 1/8.
	 * @param value */
	public void setReduceMul (float value) {
		this.fxaaReduceMul = value;
		setParam(Param.FxaaReduceMul, this.fxaaReduceMul);
	}

	/** Sets the parameter. The default value is 8;
	 * @param value */
	public void setSpanMax(float value) {
		this.fxaaSpanMax = value;
		setParam(Param.FxaaSpanMax, this.fxaaSpanMax);
	}

    @Override
    public void resize(int width, int height) {
		this.viewportInverse.set(1f / width, 1f / height);
		setParam(Param.ViewportInverse, this.viewportInverse);
    }

    @Override
	public void rebind () {
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
