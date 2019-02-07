
package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.filters.Copy;
import com.crashinvaders.vfx.filters.MotionFilter;

/** A motion blur effect which draws the last frame with a lower opacity. The result is then stored as the next last frame to
 * create the trail effect.
 * @author Toni Sagrista */
public class MotionBlurEffect extends PostProcessorEffect {
	private final MotionFilter motionFilter;
	private final Copy copyFilter;
	private FboWrapper localFbo;

	public MotionBlurEffect(Pixmap.Format pixelFormat, float blurOpacity) {
		motionFilter = new MotionFilter();
		copyFilter = new Copy();
		localFbo = new FboWrapper(pixelFormat);

		motionFilter.setBlurOpacity(blurOpacity);
	}

	@Override
	public void resize(int width, int height) {
		motionFilter.resize(width, height);
		copyFilter.resize(width, height);

		localFbo.initialize(width, height);
		localFbo.getFbo().getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
	}

	public MotionBlurEffect blurOpacity(float blurOpacity) {
		motionFilter.setBlurOpacity(blurOpacity);
		return this;
	}

	@Override
	public void dispose () {
		motionFilter.dispose();
		copyFilter.dispose();

		if (localFbo != null) {
			localFbo.dispose();
		}
	}

	@Override
	public void rebind () {
		motionFilter.rebind();
		copyFilter.rebind();
	}

	@Override
	public void render(FboWrapper src, FboWrapper dest) {
		motionFilter.setInput(src).setOutput(localFbo).render();
		motionFilter.setLastFrameTexture(localFbo.getFbo().getColorBufferTexture());
		copyFilter.setInput(localFbo).setOutput(dest).render();
	}

}
