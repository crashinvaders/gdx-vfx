
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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBufferQueue;

/** A motion blur effect which draws the last frame with a lower opacity.
 * The result is then stored as the next last frame to create the trail effect. */
public class MotionBlurFilter extends AbstractVfxFilter {

	private final MixFilter mixFilter;
	private final CopyFilter copyFilter;

	private final VfxFrameBufferQueue localBuffer;

	private boolean firstFrameRendered = false;

	public MotionBlurFilter(Pixmap.Format pixelFormat, MixFilter.Method mixMethod, float blurFactor) {
		mixFilter = new MixFilter(mixMethod);
		mixFilter.setMixFactor(blurFactor);

		copyFilter = new CopyFilter();

		localBuffer = new VfxFrameBufferQueue(pixelFormat,
				// On WebGL (GWT) we cannot render from/into the same texture simultaneously.
				// Will use ping-pong approach to avoid "writing into itself".
				Gdx.app.getType() == Application.ApplicationType.WebGL ? 2 : 1
		);
	}

	@Override
	public void dispose() {
		mixFilter.dispose();
		copyFilter.dispose();
		localBuffer.dispose();
	}

	@Override
	public void resize(int width, int height) {
		mixFilter.resize(width, height);
		copyFilter.resize(width, height);
		localBuffer.resize(width, height);

		firstFrameRendered = false;
	}

	@Override
	public void rebind() {
		mixFilter.rebind();
		copyFilter.rebind();
		localBuffer.rebind();
	}

	@Override
	public void render(VfxRenderContext context, PingPongBuffer pingPongBuffer) {
		VfxFrameBuffer prevFrame = this.localBuffer.changeToNext();
		if (!firstFrameRendered) {
			// Mix filter requires two frames to render, so we gonna skip the first call.
			copyFilter.render(context, pingPongBuffer.getSrcBuffer(), prevFrame);
			pingPongBuffer.swap();
			firstFrameRendered = true;
			return;
		}

		mixFilter.setSecondInput(prevFrame);
		mixFilter.render(context, pingPongBuffer);
		copyFilter.render(context, pingPongBuffer.getDstBuffer(), prevFrame);
	}
}