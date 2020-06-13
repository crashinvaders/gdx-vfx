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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

//TODO Add javadoc.
public class VfxFrameBufferPool implements Disposable {
    private static final String TAG = VfxFrameBufferPool.class.getSimpleName();

    /** The highest number of free buffer instances. Can be reset any time. */
    public int freePeak;

    /** A collection of all the buffers created and managed by the pool. */
    protected final Array<VfxFrameBuffer> managedBuffers;
    /** A pool of spare buffers that are ready to be obtained. */
    protected final Array<VfxFrameBuffer> freeBuffers;

    private int width;
    private int height;
    private Pixmap.Format pixelFormat;

    private Texture.TextureWrap textureWrapU = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureWrap textureWrapV = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureFilter textureFilterMin = Texture.TextureFilter.Nearest;
    private Texture.TextureFilter textureFilterMag = Texture.TextureFilter.Nearest;

    private boolean disposed = false;

    public VfxFrameBufferPool() {
        this(Pixmap.Format.RGBA8888, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), 16);
    }

    public VfxFrameBufferPool(Pixmap.Format pixelFormat, int width, int height, int initialCapacity) {
        this.width = width;
        this.height = height;
        this.pixelFormat = pixelFormat;

        this.managedBuffers = new Array<>(false, initialCapacity);
        this.freeBuffers = new Array<>(false, initialCapacity);
    }

    @Override
    public void dispose() {
        if (managedBuffers.size != freeBuffers.size) {
            int unfreedBufferAmount = managedBuffers.size - freeBuffers.size;
            Gdx.app.error(TAG, "At the moment of disposal, " +
                    "the pool still has some managed buffers unfreed (" + unfreedBufferAmount +"). " +
                    "Someone's using them and hasn't freed?");
        }

        disposed = true;

        for (int i = 0; i < managedBuffers.size; i++) {
            managedBuffers.get(i).dispose();
        }
        managedBuffers.clear();
        freeBuffers.clear();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        cleanupInvalid();
    }

    /**
     * Returns a buffer from this pool. The buffer may be
     * new (from {@link #createBuffer()}) or reused (previously {@link #free(VfxFrameBuffer) freed}).
     */
    public VfxFrameBuffer obtain() {
        if (disposed) throw new IllegalStateException("Instance is already disposed");

        return freeBuffers.size == 0 ? createBuffer() : freeBuffers.pop();
    }

    /**
     * Returns the buffer in the free pool, making it eligible for {@link #obtain()}.
     * <p>
     * For performance sake, the pool does not check if the buffer is already freed, so the same buffer must not be freed multiple times.
     */
    public void free(VfxFrameBuffer buffer) {
        if (disposed) throw new IllegalStateException("Instance is already disposed");
        if (buffer == null) throw new IllegalArgumentException("buffer cannot be null.");

        if (!validateBuffer(buffer)) {
            managedBuffers.removeValue(buffer, true);
            buffer.dispose();
            return;
        }

        freeBuffers.add(buffer);
        freePeak = Math.max(freePeak, freeBuffers.size);
        resetBuffer(buffer);
    }

    /** Removes all the free buffers from the pool. */
    public void clearFree() {
        for (int i = 0; i < freeBuffers.size; i++) {
            VfxFrameBuffer buffer = freeBuffers.get(i);
            managedBuffers.removeValue(buffer, true);
            buffer.dispose();
        }
        freeBuffers.clear();
    }

    /** @return the number of the free buffers available. */
    public int getFreeCount() {
        return freeBuffers.size;
    }

    protected VfxFrameBuffer createBuffer() {
        VfxFrameBuffer buffer = new VfxFrameBuffer(pixelFormat);
        buffer.initialize(width, height);
        managedBuffers.add(buffer);
        return buffer;
    }

    /** Called when a buffer is freed to clear the state of the buffer for possible later reuse. */
    protected void resetBuffer(VfxFrameBuffer buffer) {
        buffer.clearRenderers();

        // Reset texture params to the default ones.
        Texture texture = buffer.getTexture();
        texture.setWrap(textureWrapU, textureWrapV);
        texture.setFilter(textureFilterMin, textureFilterMag);
    }

    protected boolean validateBuffer(VfxFrameBuffer buffer) {
        FrameBuffer fbo = buffer.getFbo();
        return buffer.isInitialized() &&
                this.width == fbo.getWidth() &&
                this.height == fbo.getHeight() &&
                this.pixelFormat == buffer.getPixelFormat();
    }

    /** Checks if the buffers are valid. Those which are not will be reconstructed or deleted if they are free. */
    protected void cleanupInvalid() {
        for (int i = 0; i < managedBuffers.size; i++) {
            VfxFrameBuffer buffer = managedBuffers.get(i);
            if (!validateBuffer(buffer)) {
                // Buffer is invalid - means we have to reinitialize it according to the current configuration.
                // FBO reinitialization is an expensive operation, no reason doing it for the buffers that are currently not in use.
                // So in case a buffer is free, we just dispose and delete it.
                boolean wasFree = freeBuffers.removeValue(buffer, true);
                if (wasFree) {
                    managedBuffers.removeValue(buffer, true);
                    buffer.dispose();
                } else {
                    buffer.initialize(width, height);
                }
            }
        }
    }

    public void setTextureParams(Texture.TextureWrap textureWrapU,
                                 Texture.TextureWrap textureWrapV,
                                 Texture.TextureFilter textureFilterMin,
                                 Texture.TextureFilter textureFilterMag) {
        this.textureWrapU = textureWrapU;
        this.textureWrapV = textureWrapV;
        this.textureFilterMin = textureFilterMin;
        this.textureFilterMag = textureFilterMag;

        // Update the free textures'.
        for (int i = 0; i < freeBuffers.size; i++) {
            Texture texture = freeBuffers.get(i).getTexture();
            texture.setWrap(textureWrapU, textureWrapV);
            texture.setFilter(textureFilterMin, textureFilterMag);
        }
    }
}
