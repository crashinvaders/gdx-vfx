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

package com.crashinvaders.vfx.framebuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;

public class RegularPingPongBuffer extends PingPongBuffer {

    /**
     * Initializes ping-pong buffer with the size of the LibGDX client's area (usually window size).
     * If you use different OpenGL viewport, better use {@link #RegularPingPongBuffer(Pixmap.Format, int, int)}
     * and specify the size manually.
     * @param fbFormat Pixel format of encapsulated {@link VfxFrameBuffer}s.
     */
    public RegularPingPongBuffer(Pixmap.Format fbFormat) {
        this(fbFormat, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    /**
     * Initializes ping-pong buffer with the given size.
     * @param fbFormat Pixel format of encapsulated {@link VfxFrameBuffer}s.
     */
    public RegularPingPongBuffer(Pixmap.Format fbFormat, int width, int height) {
        this.bufDst = new VfxFrameBuffer(fbFormat);
        this.bufSrc = new VfxFrameBuffer(fbFormat);
        resize(width, height);
    }
}
