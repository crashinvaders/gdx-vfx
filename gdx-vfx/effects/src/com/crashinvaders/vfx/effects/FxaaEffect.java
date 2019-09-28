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

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Implements the fast approximate anti-aliasing.
 * Very fast and useful for combining with other post-processing effects.
 * @author Toni Sagrista
 * @author metaphore
 */
public final class FxaaEffect extends ShaderVfxEffect {

	private static final String U_TEXTURE0 = "u_texture0";
	private static final String U_VIEWPORT_INVERSE = "u_viewportInverse";
	private static final String U_FXAA_REDUCE_MIN = "u_fxaaReduceMin";
	private static final String U_FXAA_REDUCE_MUL = "u_fxaaReduceMul";
	private static final String U_FXAA_SPAN_MAX = "u_fxaaSpanMax";

	private final Vector2 viewportInverse = new Vector2();
	private float fxaaReduceMin;
	private float fxaaReduceMul;
	private float fxaaSpanMax;

	public FxaaEffect() {
		this(1f/128f, 1f/8f, 8f, true);
	}

	public FxaaEffect(float fxaaReduceMin, float fxaaReduceMul, float fxaaSpanMax, boolean supportAlpha) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/fxaa.frag"),
				supportAlpha ? "#define SUPPORT_ALPHA" : ""));
		this.fxaaReduceMin = fxaaReduceMin;
		this.fxaaReduceMul = fxaaReduceMul;
		this.fxaaSpanMax = fxaaSpanMax;
		rebind();
	}

	@Override
	public void rebind() {
		super.rebind();
		program.begin();
		program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
		program.setUniformf(U_VIEWPORT_INVERSE, viewportInverse);
		program.setUniformf(U_FXAA_REDUCE_MIN, fxaaReduceMin);
		program.setUniformf(U_FXAA_REDUCE_MUL, fxaaReduceMul);
		program.setUniformf(U_FXAA_SPAN_MAX, fxaaSpanMax);
		program.end();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		this.viewportInverse.set(1f / width, 1f / height);
		setUniform(U_VIEWPORT_INVERSE, this.viewportInverse);
	}

	/** Sets the parameter. The default value is 1/128.
	 * @param value */
	public void setReduceMin (float value) {
		this.fxaaReduceMin = value;
		setUniform(U_FXAA_REDUCE_MIN, this.fxaaReduceMin);
	}

	/** Sets the parameter. The default value is 1/8.
	 * @param value */
	public void setReduceMul (float value) {
		this.fxaaReduceMul = value;
		setUniform(U_FXAA_REDUCE_MUL, this.fxaaReduceMul);
	}

	/** Sets the parameter. The default value is 8;
	 * @param value */
	public void setSpanMax(float value) {
		this.fxaaSpanMax = value;
		setUniform(U_FXAA_SPAN_MAX, this.fxaaSpanMax);
	}
}
