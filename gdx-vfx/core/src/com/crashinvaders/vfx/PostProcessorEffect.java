/*******************************************************************************
 * Copyright 2012 bmanuel
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

package com.crashinvaders.vfx;

import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.common.framebuffer.FboWrapper;

/**
 * This interface defines the base class for the concrete implementation of post-processor effects. An effect is considered
 * enabled by default.
 *
 * @author bmanuel
 */
public abstract class PostProcessorEffect implements Disposable {
    protected boolean enabled = true;

    /**
     * This method will be called once effect will be added to {@link PostProcessor}.
     * Also it will be called on every application resize as usual.
     */
    public abstract void resize(int width, int height);

    /**
     * Concrete objects shall be responsible to recreate or rebind its own resources whenever its needed, usually when the OpenGL
     * context is lost. Eg., framebuffer textures should be updated and shader parameters should be reuploaded/rebound.
     */
    public abstract void rebind();

    /** Concrete objects shall implements its own rendering, given the source and destination buffers. */
    public abstract void render(final FboWrapper src, final FboWrapper dest);

    /** Whether or not this effect is enabled and should be processed */
    public boolean isEnabled() {
        return enabled;
    }

    /** Sets this effect enabled or not */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

//	/** Convenience method to forward the call to the PostProcessor object while still being a non-publicly accessible method */
//	protected void restoreViewport (FrameBuffer dest) {
//		PostProcessor.restoreViewport(dest);
//	}
}
