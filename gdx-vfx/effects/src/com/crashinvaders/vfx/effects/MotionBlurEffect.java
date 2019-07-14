
package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.common.framebuffer.FboWrapperQueue;
import com.crashinvaders.vfx.filters.Copy;
import com.crashinvaders.vfx.filters.MotionBlurFilter;
import com.crashinvaders.vfx.filters.MotionBlurFilter.BlurFunction;

/** A motion blur effect which draws the last frame with a lower opacity. The result is then stored as the next last frame to
 * create the trail effect.
 * @author Toni Sagrista */
public class MotionBlurEffect extends PostProcessorEffect {
	private final MotionBlurFilter motionBlurFilter;
	private final Copy copyFilter;
	private final FboWrapperQueue localBuffer;

	public MotionBlurEffect(Pixmap.Format pixelFormat, BlurFunction blurFunction, float blurOpacity) {
		motionBlurFilter = new MotionBlurFilter(blurFunction);
		motionBlurFilter.setBlurOpacity(blurOpacity);

		copyFilter = new Copy();

		localBuffer = new FboWrapperQueue(pixelFormat,
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

	public MotionBlurEffect blurOpacity(float blurOpacity) {
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
	public void render(FboWrapper src, FboWrapper dest) {
		FboWrapper prevFrame = this.localBuffer.changeToNext();
		motionBlurFilter.setInput(src).setOutput(prevFrame).render();
		motionBlurFilter.setLastFrameTexture(prevFrame.getFbo().getColorBufferTexture());
		copyFilter.setInput(prevFrame).setOutput(dest).render();
	}

}
