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

/** Normal filtered anti-aliasing filter.
 * @author Toni Sagrista */
public final class NfaaFilter extends VfxFilter<NfaaFilter> {

	private final Vector2 viewportInverse = new Vector2();

	public enum Param implements Parameter {
		Texture("u_texture0", 0),
		ViewportInverse("u_viewportInverse", 2);

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

	public NfaaFilter (boolean supportAlpha) {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/nfaa.frag"),
				supportAlpha ? "#define SUPPORT_ALPHA" : ""));
	}

    @Override
    public void resize(int width, int height) {
		this.viewportInverse.set(1f / width, 1f / height);
		setParam(Param.ViewportInverse, this.viewportInverse);
    }

    @Override
	public void rebind () {
		// Re-implement super to batch every parameter
		setParams(Param.Texture, u_texture0);
		setParams(Param.ViewportInverse, viewportInverse);
		endParams();
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);
	}
}
