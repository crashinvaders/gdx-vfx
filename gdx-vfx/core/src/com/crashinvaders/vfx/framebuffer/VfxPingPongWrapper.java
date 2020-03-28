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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Pool;

//TODO Update the javadocs.
/**
 * Encapsulates a pair of {@link VfxFrameBuffer}s with the ability to swap between them.
 * <p>
 *
 * Upon {@link #begin()} the buffer is reset to a known initial state, this is usually done just before the first usage of the buffer.
 * Subsequent {@link #swap()} calls will initiate writing to the next available buffer, effectively ping-ponging between the two.
 * Chained rendering will be possible by retrieving the
 * necessary buffers via {@link #getSrcBuffer()}, {@link #getDstBuffer()}, {@link #getSrcTexture()} or
 * {@link #getDstTexture}.
 * <br/>
 * When rendering is finished, {@link #end()} should be called to stop capturing.
 * <p>
 *
 * {@link VfxPingPongWrapper} only wraps to provided buffers but doesn't manage them.
 * So it's your responsibility to call {@link VfxFrameBuffer#initialize(int, int)} and {@link VfxFrameBuffer#dispose()} for them.
 * <br/>
 * You also may use the benefits of {@link VfxFrameBufferPool} if you have one
 * (all the {@link com.crashinvaders.vfx.effects.ChainVfxEffect} have access to one from {@link com.crashinvaders.vfx.VfxRenderContext}).
 * <br/>
 * Simply use {@link VfxPingPongWrapper (VfxFrameBufferPool)} or {@link #initialize(VfxFrameBufferPool)} and the buffers will be created,
 * resized and destroyed for you,
 * just don't forget to call {@link VfxPingPongWrapper#reset()} when you're done with this instance.
 *
 * @author metaphore
 */
public class VfxPingPongWrapper implements Pool.Poolable {

    protected VfxFrameBuffer bufDst;
    protected VfxFrameBuffer bufSrc;

    /** Where capturing is started. Should be true between {@link #begin()} and {@link #end()}. */
    protected boolean capturing;

    protected VfxFrameBufferPool bufferPool = null;

    public VfxPingPongWrapper() {
    }

    public VfxPingPongWrapper(VfxFrameBufferPool bufferPool) {
        initialize(bufferPool);
    }

    public VfxPingPongWrapper(VfxFrameBuffer bufDst, VfxFrameBuffer bufSrc) {
        initialize(bufSrc, bufDst);
    }

    public VfxPingPongWrapper initialize(VfxFrameBufferPool bufferPool) {
        this.bufferPool = bufferPool;
        VfxFrameBuffer bufDst = bufferPool.obtain();
        VfxFrameBuffer bufSrc = bufferPool.obtain();
        return initialize(bufDst, bufSrc);
    }

    public VfxPingPongWrapper initialize(VfxFrameBuffer bufSrc, VfxFrameBuffer bufDst) {
        if (capturing) {
            throw new IllegalStateException("Ping pong buffer cannot be initialized during capturing stage. It seems the instance is already initialized.");
        }
        if (isInitialized()) {
            reset();
        }
        this.bufSrc = bufSrc;
        this.bufDst = bufDst;
        return this;
    }

    @Override
    public void reset() {
        if (capturing) {
            throw new IllegalStateException("Ping pong buffer cannot be reset during capturing stage. Forgot to call end()?");
        }

        // If the buffers were create using VfxBufferPool, we shall free them properly.
        if (bufferPool != null) {
            bufferPool.free(bufSrc);
            bufferPool.free(bufDst);
            bufferPool = null;
        }

        bufSrc = null;
        bufDst = null;
    }

    public boolean isInitialized() {
        return bufDst != null && bufSrc != null;
    }

    /**
     * Start capturing into the destination buffer.
     * To swap buffers during capturing, call {@link #swap()}.
     * {@link #end()} shall be called after rendering to ping-pong buffer is done.
     */
    public void begin() {
        if (capturing) {
            throw new IllegalStateException("Ping pong buffer is already in capturing state.");
        }

        capturing = true;
        bufDst.begin();
    }

    /**
     * Finishes ping-ponging. Must be called after {@link #begin()}.
     **/
    public void end() {
        if (!capturing) {
            throw new IllegalStateException("Ping pong is not in capturing state. You should call begin() before calling end().");
        }
        bufDst.end();
        capturing = false;
    }

    /**
     * Swaps source/target buffers.
     * May be called outside of capturing state.
     */
    public void swap() {
        if (capturing) {
            bufDst.end();
        }

        // Swap buffers
        VfxFrameBuffer tmp = this.bufDst;
        bufDst = bufSrc;
        bufSrc = tmp;

        if (capturing) {
            bufDst.begin();
        }
    }

    public boolean isCapturing() {
        return capturing;
    }

    /** @return the source texture of the current ping-pong chain. */
    public Texture getSrcTexture() {
        return bufSrc.getFbo().getColorBufferTexture();
    }

    /** @return the source buffer of the current ping-pong chain. */
    public VfxFrameBuffer getSrcBuffer() {
        return bufSrc;
    }

    /** @return the result's texture of the latest {@link #swap()}. */
    public Texture getDstTexture() {
        return bufDst.getFbo().getColorBufferTexture();
    }

    /** @return Returns the result's buffer of the latest {@link #swap()}. */
    public VfxFrameBuffer getDstBuffer() {
        return bufDst;
    }

    /** Cleans up managed {@link VfxFrameBuffer}s' with the color specified. */
    public void cleanUpBuffers(Color clearColor) {
        cleanUpBuffers(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
    }

    /** Cleans up managed {@link VfxFrameBuffer}s' with the color specified. */
    public void cleanUpBuffers(float r, float g, float b, float a) {
        final boolean wasCapturing = this.capturing;

        if (!wasCapturing) { begin(); }

        Gdx.gl.glClearColor(r, g, b, a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        swap();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!wasCapturing) { end(); }
    }
}