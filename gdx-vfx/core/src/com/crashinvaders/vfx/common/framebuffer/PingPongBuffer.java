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

package com.crashinvaders.vfx.common.framebuffer;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

/**
 * Encapsulates a framebuffer with the ability to ping-pong between two buffers.
 * <p>
 * Upon {@link #begin()} the buffer is reset to a known initial state, this is usually done just before the first usage of the
 * buffer.
 * <p>
 * Subsequent {@link #capture()} calls will initiate writing to the next available buffer, returning the previously used one,
 * effectively ping-ponging between the two. Until {@link #end()} is called, chained rendering will be possible by retrieving the
 * necessary buffers via {@link #getSourceTexture()}, {@link #getSourceBuffer()}, {@link #getResultTexture()} or
 * {@link #getResultBuffer}.
 * <p>
 * When finished, {@link #end()} should be called to stop capturing. When the OpenGL context is lost, {@link #rebind()} should be
 * called.
 *
 * @author bmanuel
 */
public final class PingPongBuffer {
    public final FboWrapper buffer1, buffer2;
    public Texture texture1, texture2;

    // internal state
    private Texture texResult, texSrc;
    private FboWrapper bufResult, bufSrc;
    private boolean writeState, pending1, pending2;

    private TextureWrap wrapU = TextureWrap.ClampToEdge;
    private TextureWrap wrapV = TextureWrap.ClampToEdge;
    private TextureFilter filterMin = TextureFilter.Nearest;
    private TextureFilter filterMag = TextureFilter.Nearest;

    public PingPongBuffer(Format fbFormat, int width, int height) {
        this.buffer1 = new FboWrapper(fbFormat);
        this.buffer2 = new FboWrapper(fbFormat);
        this.buffer1.initialize(width, height);
        this.buffer2.initialize(width, height);
        rebind();
    }

    /**
     * <b>WARNING:</b> You have to call {@link #resize(int, int)} manually before using this instance.
     */
    public PingPongBuffer(Format fbFormat) {
        this.buffer1 = new FboWrapper(fbFormat);
        this.buffer2 = new FboWrapper(fbFormat);
    }

    public void dispose() {
        buffer1.dispose();
        buffer2.dispose();
    }

    public void resize(int width, int height) {
        this.buffer1.initialize(width, height);
        this.buffer2.initialize(width, height);
        rebind();
    }

    /** When needed graphics memory could be invalidated so buffers should be rebuilt. */
    public void rebind() {
        // FBOs might be null if the instance wasn't initialized with #resize(int, int) yet.
        if (buffer1.getFbo() != null) {
            texture1 = buffer1.getFbo().getColorBufferTexture();
            texture1.setWrap(wrapU, wrapV);
            texture1.setFilter(filterMin, filterMag);
        }
        if (buffer2.getFbo() != null) {
            texture2 = buffer2.getFbo().getColorBufferTexture();
            texture2.setWrap(wrapU, wrapV);
            texture2.setFilter(filterMin, filterMag);
        }
    }

    /** Ensures the initial buffer state is always the same before starting ping-ponging. */
    public void begin() {
        pending1 = false;
        pending2 = false;
        writeState = true;

        texSrc = texture1;
        bufSrc = buffer1;
        texResult = texture2;
        bufResult = buffer2;
    }

    /**
     * Starts and/or continue ping-ponging,
     * begin capturing on the next available buffer,
     * returns the result of the previous.
     * @return the Texture containing the result.
     */
    public Texture capture() {
        endPending();

        if (writeState) {
            // set src
            texSrc = texture1;
            bufSrc = buffer1;

            // set result
            texResult = texture2;
            bufResult = buffer2;

            // write to other
            pending2 = true;
            buffer2.begin();
        } else {
            texSrc = texture2;
            bufSrc = buffer2;

            texResult = texture1;
            bufResult = buffer1;

            pending1 = true;
            buffer1.begin();
        }

        writeState = !writeState;
        return texSrc;
    }

    /** Finishes ping-ponging, must always be called after a call to {@link #capture()} */
    public void end() {
        endPending();
    }

    /** @return the source texture of the current ping-pong chain. */
    public Texture getSourceTexture() {
        return texSrc;
    }

    /** @return the source buffer of the current ping-pong chain. */
    public FboWrapper getSourceBuffer() {
        return bufSrc;
    }

    /** @return the result's texture of the latest {@link #capture()}. */
    public Texture getResultTexture() {
        return texResult;
    }

    /** @return Returns the result's buffer of the latest {@link #capture()}. */
    public FboWrapper getResultBuffer() {
        return bufResult;
    }

    public void setTextureParams(TextureWrap u, TextureWrap v, TextureFilter min, TextureFilter mag) {
        wrapU = u;
        wrapV = v;
        filterMin = min;
        filterMag = mag;
        rebind();
    }

    public void addRenderer(FboWrapper.Renderer renderer) {
        buffer1.addRenderer(renderer);
        buffer2.addRenderer(renderer);
    }

    public void removeRenderer(FboWrapper.Renderer renderer) {
        buffer1.removeRenderer(renderer);
        buffer2.removeRenderer(renderer);
    }

    public void clearRenderers() {
        buffer1.clearRenderers();
        buffer2.clearRenderers();
    }

    // internal use
    // finish writing to the buffers, mark as not pending anymore.
    private void endPending() {
        if (pending1) {
            buffer1.end();
            pending1 = false;
        }
        if (pending2) {
            buffer2.end();
            pending2 = false;
        }
    }
}
