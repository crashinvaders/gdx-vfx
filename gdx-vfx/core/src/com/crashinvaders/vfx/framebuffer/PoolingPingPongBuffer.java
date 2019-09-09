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

import com.badlogic.gdx.graphics.Texture;

public class PoolingPingPongBuffer extends PingPongBuffer {

    private VfxFrameBufferPool pool;
    private boolean ownBuffers = false;

    public PoolingPingPongBuffer() {
        this(null);
    }

    public PoolingPingPongBuffer(VfxFrameBufferPool pool) {
        this.pool = pool;
    }

    public void setPool(VfxFrameBufferPool pool) {
        this.pool = pool;
    }

    public VfxFrameBufferPool getPool() {
        return pool;
    }

    public void obtainBuffers() {
        if (ownBuffers) return;
        if (pool == null) throw new IllegalStateException("Pool is not set for the instance.");

        bufDst = pool.obtain();
        bufSrc = pool.obtain();

        ownBuffers = true;
    }

    public void freeBuffers() {
        if (!ownBuffers) return;
        if (capturing) throw new IllegalStateException("Cannot free buffers during capturing phase.");
        if (pool == null) throw new IllegalStateException("Pool is not set for the instance.");

        pool.free(bufDst);
        pool.free(bufSrc);
        bufDst = null;
        bufSrc = null;

        ownBuffers = false;
    }

    public boolean isOwnBuffers() {
        return ownBuffers;
    }

    @Override
    public void resize(int width, int height) {
        if (ownBuffers) {
            super.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (ownBuffers) {
            freeBuffers();
        }
    }

    @Override
    public void rebind() {
        if (ownBuffers) {
            super.rebind();
        }
    }

    @Override
    public void begin() {
        ensureOwnBuffers();
        super.begin();
    }

    @Override
    public void end() {
        super.end();
    }

    @Override
    public void swap() {
        super.swap();
    }

    @Override
    public Texture getSrcTexture() {
        ensureOwnBuffers();
        return super.getSrcTexture();
    }

    @Override
    public VfxFrameBuffer getSrcBuffer() {
        ensureOwnBuffers();
        return super.getSrcBuffer();
    }

    @Override
    public Texture getDstTexture() {
        ensureOwnBuffers();
        return super.getDstTexture();
    }

    @Override
    public VfxFrameBuffer getDstBuffer() {
        ensureOwnBuffers();
        return super.getDstBuffer();
    }

    @Override
    public void setTextureParams(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        if (ownBuffers) {
            super.setTextureParams(u, v, min, mag);
        }
    }

    @Override
    public void addRenderer(VfxFrameBuffer.Renderer renderer) {
        if (ownBuffers) {
            super.addRenderer(renderer);
        }
    }

    @Override
    public void removeRenderer(VfxFrameBuffer.Renderer renderer) {
        if (ownBuffers) {
            super.removeRenderer(renderer);
        }
    }

    @Override
    public void clearRenderers() {
        if (ownBuffers) {
            super.clearRenderers();
        }
    }

    @Override
    public void cleanUpBuffers(float r, float g, float b, float a) {
        super.cleanUpBuffers(r, g, b, a);
    }

    private void ensureOwnBuffers() {
        if (!ownBuffers) {
            throw new IllegalStateException("Internal pooled buffers are not provided. Consider calling obtainBuffers() before this call.");
        }
    }
}
