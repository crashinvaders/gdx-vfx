/*******************************************************************************
 * Copyright 2012 tsagrista
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

import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.filters.NfaaFilterOld;

/** Implements the normal filter anti-aliasing. Very fast and useful for combining with other post-processing effects.
 * @author Toni Sagrista */
public final class NfaaEffect extends VfxEffectOld {

	private final NfaaFilterOld nfaaFilter;

	public NfaaEffect() {
		this(false);
	}

	public NfaaEffect(boolean supportAlpha) {
		nfaaFilter = new NfaaFilterOld(supportAlpha);
	}

	@Override
	public void dispose() {
		nfaaFilter.dispose();
	}

	@Override
	public void rebind() {
		nfaaFilter.rebind();
	}

	@Override
	public void resize(int width, int height) {
		nfaaFilter.resize(width, height);
	}

	@Override
	public void render(ViewportQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
		nfaaFilter.setInput(src).setOutput(dst).render(mesh);
	}
}
