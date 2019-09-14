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
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class RadialDistortionFilterOld extends VfxFilterOld<RadialDistortionFilterOld> {

	public enum Param implements Parameter {
		Texture0("u_texture0", 0),
		Distortion("distortion", 0),
		Zoom("zoom", 0);

		final String mnemonic;
		final int elementSize;

		Param(String m, int elementSize) {
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

	private float zoom = 1f;
	private float distortion = 0.3f;

	public RadialDistortionFilterOld() {
		super(VfxGLUtils.compileShader(
				Gdx.files.classpath("shaders/screenspace.vert"),
				Gdx.files.classpath("shaders/radial-distortion.frag")));
		rebind();
	}

	public float getDistortion () {
		return distortion;
	}

	public void setDistortion (float distortion) {
		this.distortion = distortion;
		setParam(Param.Distortion, this.distortion);
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
		setParams(Param.Texture0, u_texture0);
		setParams(Param.Distortion, distortion);
		setParams(Param.Zoom, zoom);
		endParams();
	}
}
