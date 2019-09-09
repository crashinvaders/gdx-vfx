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
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.Iterator;

public class VfxFrameBufferPool implements Disposable {

    /** The highest number of free objects. Can be reset any time. */
    public int peak;

    protected final Array<VfxFrameBuffer> freeBuffers;

    private int width;
    private int height;
    private Pixmap.Format pixelFormat;

    private boolean disposed = false;

    public VfxFrameBufferPool() {
        this(Pixmap.Format.RGBA8888, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), 16);
    }

    public VfxFrameBufferPool(Pixmap.Format pixelFormat, int width, int height, int initialCapacity) {
        this.width = width;
        this.height = height;
        this.pixelFormat = pixelFormat;

        this.freeBuffers = new Array<>(false, initialCapacity);
    }

    @Override
    public void dispose() {
        disposed = true;
        for (int i = 0; i < freeBuffers.size; i++) {
            freeBuffers.get(i).dispose();
        }
        freeBuffers.clear();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        validateAndCleanFreeBuffers();
    }

    /**
     * Returns a buffer from this pool. The buffer may be new (from {@link #createBuffer()}) or reused (previously {@link
     * #free(VfxFrameBuffer) freed}).
     */
    public VfxFrameBuffer obtain() {
        if (disposed) throw new IllegalStateException("Instance is already disposed");

        return freeBuffers.size == 0 ? createBuffer() : freeBuffers.pop();
    }

    /**
     * Puts the specified buffer in the pool, making it eligible to be returned by {@link #obtain()}.
     * <p>
     * The pool does not check if a buffer is already freed, so the same buffer must not be freed multiple times.
     */
    public void free(VfxFrameBuffer buffer) {
        if (buffer == null) throw new IllegalArgumentException("buffer cannot be null.");

        if (disposed || !validateBuffer(buffer)) {
            buffer.dispose();
            return;
        }

        freeBuffers.add(buffer);
        peak = Math.max(peak, freeBuffers.size);
        reset(buffer);
    }

    /**
     * Puts the specified buffers in the pool. Null buffers within the array are silently ignored.
     * <p>
     * The pool does not check if a buffer is already freed, so the same buffer must not be freed multiple times.
     * @see #free(VfxFrameBuffer)
     */
    public void freeAll(Array<VfxFrameBuffer> buffers) {
        if (buffers == null) throw new IllegalArgumentException("buffers cannot be null.");
        Array<VfxFrameBuffer> freeBuffers = this.freeBuffers;
        for (int i = 0; i < buffers.size; i++) {
            VfxFrameBuffer buffer = buffers.get(i);

            if (buffer == null) continue;

            if (disposed || !validateBuffer(buffer)) {
                buffer.dispose();
                continue;
            }
            freeBuffers.add(buffer);
            reset(buffer);
        }
        peak = Math.max(peak, freeBuffers.size);
    }

    /** Removes all free buffers from this pool. */
    public void clear() {
        freeBuffers.clear();
    }

    /** The number of buffers available to be obtained. */
    public int getFree() {
        return freeBuffers.size;
    }

    protected VfxFrameBuffer createBuffer() {
        VfxFrameBuffer buffer = new VfxFrameBuffer(pixelFormat);
        buffer.initialize(width, height);
        return buffer;
    }

    /** Called when a buffer is freed to clear the state of the buffer for possible later reuse. */
    protected void reset(VfxFrameBuffer buffer) {
        buffer.clearRenderers();
    }

    private void validateAndCleanFreeBuffers() {
        Iterator<VfxFrameBuffer> iter = freeBuffers.iterator();
        while (iter.hasNext()) {
            VfxFrameBuffer buffer = iter.next();
            if (!validateBuffer(buffer)) {
                buffer.dispose();
                iter.remove();
            }
        }
    }

    private boolean validateBuffer(VfxFrameBuffer buffer) {
        FrameBuffer fbo = buffer.getFbo();
        return buffer.isInitialized() &&
                this.width == fbo.getWidth() &&
                this.height == fbo.getHeight() &&
                this.pixelFormat == buffer.getPixelFormat();
    }
}
