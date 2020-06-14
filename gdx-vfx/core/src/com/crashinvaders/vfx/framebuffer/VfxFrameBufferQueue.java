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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Provides looped access to an array of {@link VfxFrameBuffer}.
 */
public class VfxFrameBufferQueue implements Disposable {
    private final Array<VfxFrameBuffer> buffers;
    private int currentIdx = 0;

    private Texture.TextureWrap wrapU = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureWrap wrapV = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureFilter filterMin = Texture.TextureFilter.Nearest;
    private Texture.TextureFilter filterMag = Texture.TextureFilter.Nearest;

    public VfxFrameBufferQueue(Pixmap.Format pixelFormat, int fboAmount) {
        if (fboAmount < 1) {
            throw new IllegalArgumentException("FBO amount should be a positive number.");
        }
        buffers = new Array<>(true, fboAmount);
        for (int i = 0; i < fboAmount; i++) {
            buffers.add(new VfxFrameBuffer(pixelFormat));
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < buffers.size; i++) {
            buffers.get(i).dispose();
        }
        buffers.clear();
    }

    public void resize(int width, int height) {
        for (int i = 0; i < buffers.size; i++) {
            buffers.get(i).initialize(width, height);
        }
    }

    /**
     * Restores buffer OpenGL parameters. Could be useful in case of OpenGL context loss.
     */
    public void rebind() {
        for (int i = 0; i < buffers.size; i++) {
            VfxFrameBuffer wrapper = buffers.get(i);
            // FBOs might be null if the instance wasn't initialized with #resize(int, int) yet.
            if (wrapper.getFbo() == null) continue;

            Texture texture = wrapper.getFbo().getColorBufferTexture();
            texture.setWrap(wrapU, wrapV);
            texture.setFilter(filterMin, filterMag);
        }
    }

    public VfxFrameBuffer getCurrent() {
        return buffers.get(currentIdx);
    }

    public VfxFrameBuffer changeToNext() {
        currentIdx = (currentIdx + 1) % buffers.size;
        return getCurrent();
    }

    public void setTextureParams(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        wrapU = u;
        wrapV = v;
        filterMin = min;
        filterMag = mag;
        rebind();
    }
}
