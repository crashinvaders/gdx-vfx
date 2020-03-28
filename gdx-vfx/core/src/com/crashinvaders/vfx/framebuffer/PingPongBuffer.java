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
import com.badlogic.gdx.utils.Disposable;

/**
 * Encapsulates a pair of {@link VfxFrameBuffer}s with the ability to swap between them.
 * <p>
 * Upon {@link #begin()} the buffer is reset to a known initial state, this is usually done just before the first usage of the buffer.
 * Subsequent {@link #swap()} calls will initiate writing to the next available buffer, effectively ping-ponging between the two.
 * Chained rendering will be possible by retrieving the
 * necessary buffers via {@link #getSrcBuffer()}, {@link #getDstBuffer()}, {@link #getSrcTexture()} or
 * {@link #getDstTexture}.
 * <p>
 * When rendering is finished, {@link #end()} should be called to stop capturing.
 * <p>
 * When the OpenGL context is lost, {@link #rebind()} should be called.
 *
 * @author metaphore
 */
public abstract class PingPongBuffer implements Disposable {

    protected VfxFrameBuffer bufDst;
    protected VfxFrameBuffer bufSrc;

    /** Where capturing is started. Should be true between {@link #begin()} and {@link #end()}. */
    protected boolean capturing;

    private Texture.TextureWrap wrapU = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureWrap wrapV = Texture.TextureWrap.ClampToEdge;
    private Texture.TextureFilter filterMin = Texture.TextureFilter.Nearest;
    private Texture.TextureFilter filterMag = Texture.TextureFilter.Nearest;

    public void resize(int width, int height) {
        this.bufDst.initialize(width, height);
        this.bufSrc.initialize(width, height);
        rebind();
    }

    @Override
    public void dispose() {
        this.bufDst.dispose();
        this.bufSrc.dispose();
    }

    /**
     * Restores buffer OpenGL parameters. Could be useful in case of OpenGL context loss.
     */
    public void rebind() {
        // FBOs might be null if the instance wasn't initialized with #resize(int, int) yet.
        if (bufDst.getFbo() != null) {
            Texture texture = bufDst.getFbo().getColorBufferTexture();
            texture.setWrap(wrapU, wrapV);
            texture.setFilter(filterMin, filterMag);
        }
        if (bufSrc.getFbo() != null) {
            Texture texture = bufSrc.getFbo().getColorBufferTexture();
            texture.setWrap(wrapU, wrapV);
            texture.setFilter(filterMin, filterMag);
        }
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

    public void setTextureParams(Texture.TextureWrap u, Texture.TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        wrapU = u;
        wrapV = v;
        filterMin = min;
        filterMag = mag;

        Texture texDst = bufDst.getFbo().getColorBufferTexture();
        texDst.setWrap(wrapU, wrapV);
        texDst.setFilter(filterMin, filterMag);

        Texture texSrc = bufSrc.getFbo().getColorBufferTexture();
        texSrc.setWrap(wrapU, wrapV);
        texSrc.setFilter(filterMin, filterMag);
    }

    /** @see VfxFrameBuffer#addRenderer(VfxFrameBuffer.Renderer) ) */
    public void addRenderer(VfxFrameBuffer.Renderer renderer) {
        bufDst.addRenderer(renderer);
        bufSrc.addRenderer(renderer);
    }

    /** @see VfxFrameBuffer#removeRenderer(VfxFrameBuffer.Renderer) () */
    public void removeRenderer(VfxFrameBuffer.Renderer renderer) {
        bufDst.removeRenderer(renderer);
        bufSrc.removeRenderer(renderer);
    }

    /** @see VfxFrameBuffer#clearRenderers() */
    public void clearRenderers() {
        bufDst.clearRenderers();
        bufSrc.clearRenderers();
    }

    /**
     * Cleans up managed {@link VfxFrameBuffer}s' with specified color.
     */
    public void cleanUpBuffers(Color clearColor) {
        cleanUpBuffers(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
    }

    /**
     * Cleans up managed {@link VfxFrameBuffer}s' with specified color.
     */
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
