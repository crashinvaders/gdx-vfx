
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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.framebuffer.VfxFrameBufferQueue;
import com.crashinvaders.vfx.filters.CopyFilterOld;
import com.crashinvaders.vfx.filters.MotionBlurFilterOld;
import com.crashinvaders.vfx.filters.MotionBlurFilterOld.BlurFunction;

/** A motion blur effect which draws the last frame with a lower opacity. The result is then stored as the next last frame to
 * create the trail effect.
 * @author Toni Sagrista */
public class MotionBlurEffect extends VfxEffectOld {
	private final MotionBlurFilterOld motionBlurFilter;
	private final CopyFilterOld copyFilter;
	private final VfxFrameBufferQueue localBuffer;

	public MotionBlurEffect(Pixmap.Format pixelFormat, BlurFunction blurFunction, float blurOpacity) {
		motionBlurFilter = new MotionBlurFilterOld(blurFunction);
		motionBlurFilter.setBlurOpacity(blurOpacity);

		copyFilter = new CopyFilterOld();

		localBuffer = new VfxFrameBufferQueue(pixelFormat,
				// On WebGL (GWT) we cannot render from/into the same texture simultaneously.
				// Will use ping-pong approach to avoid "writing into itself".
				Gdx.app.getType() == Application.ApplicationType.WebGL ? 2 : 1
		);
	}

	@Override
	public void resize(int width, int height) {
		motionBlurFilter.resize(width, height);
		copyFilter.resize(width, height);
		localBuffer.resize(width, height);
	}

	public MotionBlurEffect setBlurOpacity(float blurOpacity) {
		motionBlurFilter.setBlurOpacity(blurOpacity);
		return this;
	}

	@Override
	public void dispose() {
		motionBlurFilter.dispose();
		copyFilter.dispose();
		localBuffer.dispose();
	}

	@Override
	public void rebind() {
		motionBlurFilter.rebind();
		copyFilter.rebind();
		localBuffer.rebind();
	}

	@Override
	public void render(ViewportQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
		VfxFrameBuffer prevFrame = this.localBuffer.changeToNext();
		motionBlurFilter.setInput(src).setOutput(prevFrame).render(mesh);
		motionBlurFilter.setLastFrameTexture(prevFrame.getFbo().getColorBufferTexture());
		copyFilter.setInput(prevFrame).setOutput(dst).render(mesh);
	}
}
