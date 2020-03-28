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

import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.VfxManager;

public interface VfxEffect extends Disposable {

    /** Whether or not this effect is disabled and shouldn't be processed */
    boolean isDisabled();

    /** Sets this effect disabled or not */
    void setDisabled(boolean enabled);
    /**
     * The method will be called on every application resize as usual.
     * Also it will be called once the filter has been added to {@link VfxManager}.
     */
    void resize(int width, int height);

    /**
     * Update any time based values.
     * @param delta in seconds.
     */
    void update(float delta);

//    /** The effects should implement their own rendering, given the source and destination buffers in the form of ping-pong wrapper. */
//    void render(VfxRenderContext context, PingPongBuffer pingPongBuffer);

    /**
     * Concrete objects shall be responsible to recreate or rebind its own resources whenever its needed,
     * usually when the OpenGL context is lost (e.g. framebuffer textures should be updated
     * and shader parameters should be reuploaded/rebound.
     */
    void rebind();
}
