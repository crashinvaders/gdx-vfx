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

package com.crashinvaders.vfx;

import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;

public interface VfxFilterNew extends Disposable {
    /**
     * This method will be called once effect will be added to {@link VfxManager}.
     * Also it will be called on every application resize as usual.
     */
    void resize(int width, int height);

    /**
     * Concrete objects shall be responsible to recreate or rebind its own resources whenever its needed, usually when the OpenGL
     * context is lost. Eg., framebuffer textures should be updated and shader parameters should be reuploaded/rebound.
     */
    void rebind();

    /** Concrete objects shall implements its own rendering, given the source and destination buffers. */
    void render(ViewportQuadMesh mesh, final VfxFrameBuffer src, final VfxFrameBuffer dst);

    /** Whether or not this effect is disabled and shouldn't be processed */
    boolean isDisabled();

    /** Sets this effect disabled or not */
    void setDisabled(boolean enabled);
}
