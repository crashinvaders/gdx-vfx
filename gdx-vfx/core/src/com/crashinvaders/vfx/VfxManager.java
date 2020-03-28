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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.framebuffer.*;
import com.crashinvaders.vfx.utils.PrioritizedArray;

/**
 * Provides a way to beginCapture the rendered scene to an off-screen buffer and to apply a chain of effects on it before rendering to
 * screen.
 * <p>
 * Effects can be added or removed via {@link #addEffect(ChainVfxEffect)} and {@link #removeEffect(ChainVfxEffect)}.
 *
 * @author metaphore
 */
public final class VfxManager implements Disposable {

    private final PrioritizedArray<ChainVfxEffect> effectsAll = new PrioritizedArray<>();
    /** Maintains a per-frame updated list of enabled effects */
    private final Array<ChainVfxEffect> effectsEnabled = new Array<>();

    /** A mesh that is shared among basic filters to draw to full screen. */
//    private final ScreenQuadMesh screenQuadMesh = new ScreenQuadMesh();
//    private final VfxFrameBufferRenderer bufferRenderer = new VfxFrameBufferRenderer();

//    private final Format fboFormat;
    private final PingPongBuffer pingPongBuffer;

    private final VfxRenderContext context;

    private boolean disabled = false;
    private boolean capturing = false;
    private boolean hasCaptured = false;
    private boolean applyingEffects = false;

    private boolean blendingEnabled = false;

    private int width, height;

    public VfxManager(Format fboFormat) {
        this(fboFormat, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    public VfxManager(Format fboFormat, int bufferWidth, int bufferHeight) {
        this.context = new VfxRenderContext(fboFormat, bufferWidth, bufferHeight);
//        this.fboFormat = fboFormat;
        this.pingPongBuffer = new RegularPingPongBuffer(fboFormat, bufferWidth, bufferHeight);
        this.width = bufferWidth;
        this.height = bufferHeight;
    }

    @Override
    public void dispose() {
        pingPongBuffer.dispose();
        context.dispose();
//        screenQuadMesh.dispose();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDisabled() {
        return disabled;
    }

    /** Sets whether or not the post-processor should be disabled */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isBlendingEnabled() {
        return blendingEnabled;
    }

    /**
     * Enables OpenGL blending for the effect chain rendering stage.
     * Disabled by default.
     */
    public void setBlendingEnabled(boolean blendingEnabled) {
        this.blendingEnabled = blendingEnabled;
    }

    /**
     * Returns the internal framebuffers' pixel format, computed from the parameters specified during construction. NOTE: the returned
     * Format will be valid after construction and NOT early!
     */
    public Format getPixelFormat() {
        return context.getPixelFormat();
    }

    public void setBufferTextureParams(TextureWrap u, TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        pingPongBuffer.setTextureParams(u, v, min, mag);
    }

    public boolean isCapturing() {
        return capturing;
    }

    public boolean isApplyingEffects() {
        return applyingEffects;
    }

    public boolean hasResult() {
        return hasCaptured;
    }

    /** @return the last active destination buffer. */
    public VfxFrameBuffer getResultBuffer() {
        return pingPongBuffer.getDstBuffer();
    }

    /** @return the internal ping-pong buffer. */
    public PingPongBuffer getPingPongBuffer() {
        return pingPongBuffer;
    }

    public VfxRenderContext getRenderContext() {
        return context;
    }

    /**
     * Adds an effect to the effect chain and transfers ownership to the VfxManager.
     * The order of the inserted effects IS important, since effects will be applied in a FIFO fashion,
     * the first added is the first being applied.
     * <p>
     * For more control over the order supply the effect with a priority - {@link #addEffect(ChainVfxEffect, int)}.
     * @see #addEffect(ChainVfxEffect, int)
     * @param effect
     */
    public void addEffect(ChainVfxEffect effect) {
        addEffect(effect, 0);
    }

    public void addEffect(ChainVfxEffect effect, int priority) {
        effectsAll.add(effect, priority);
        effect.resize(width, height);
    }

    /**
     * Removes the specified effect from the effect chain.
     */
    public void removeEffect(ChainVfxEffect effect) {
        effectsAll.remove(effect);
    }

    /**
     * Removes all effects from the effect chain.
     */
    public void removeAllEffects() {
        effectsAll.clear();
    }

    /**
     * Changes the order of the effect in the effect chain.
     */
    public void setEffectPriority(ChainVfxEffect effect, int priority) {
        effectsAll.setPriority(effect, priority);
    }

    /**
     * Cleans up managed {@link PingPongBuffer}s' with {@link Color#CLEAR}.
     */
    public void cleanUpBuffers() {
        cleanUpBuffers(Color.CLEAR);
    }

    /**
     * Cleans up managed {@link PingPongBuffer}s' with specified color.
     */
    public void cleanUpBuffers(Color color) {
        if (capturing) throw new IllegalStateException("Cannot clean up buffers when capturing.");
        if (applyingEffects) throw new IllegalStateException("Cannot clean up buffers when applying effects.");

        pingPongBuffer.cleanUpBuffers(color);
        hasCaptured = false;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        pingPongBuffer.resize(width, height);

        for (int i = 0; i < effectsAll.size(); i++) {
            effectsAll.get(i).resize(width, height);
        }
    }

    public void rebind() {
//        bufferRenderer.rebind();
        context.rebind();

        for (int i = 0; i < effectsAll.size(); i++) {
            effectsAll.get(i).rebind();
        }
    }

    public void update(float delta) {
        for (int i = 0; i < effectsAll.size(); i++) {
            effectsAll.get(i).update(delta);
        }
    }

    /**
     * Starts capturing the scene.
     *
     * @return true or false, whether or not capturing has been initiated.
     * Capturing will fail if the manager is disabled or capturing is already started.
     */
    public boolean beginCapture() {
        if (applyingEffects) {
            throw new IllegalStateException("You cannot capture when you're applying the effects.");
        }

        if (disabled) return false;
        if (capturing) return false;

        capturing = true;
        pingPongBuffer.begin();
        return true;
    }

    /**
     * Stops capturing the scene.
     * @return false if there was no capturing before that call.
     */
    public boolean endCapture() {
        if (!capturing) return false;

        hasCaptured = true;
        capturing = false;
        pingPongBuffer.end();
        return true;
    }

    /**
     * Applies the effect chain, if there is one.
     */
    public void applyEffects() {
        if (capturing) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before applying the effects.");
        }

        if (disabled) return;
        if (!hasCaptured) return;

        Array<ChainVfxEffect> effectChain = updateEnabledEffectList();

        applyingEffects = true;
        int count = effectChain.size;
        if (count > 0) {
            // Enable blending to preserve buffer's alpha values.
            if (blendingEnabled) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
            }

            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

            // Render the effect chain.
            pingPongBuffer.swap(); // Swap buffers to get captured result in src buffer.
            pingPongBuffer.begin();
            for (int i = 0; i < count; i++) {
                ChainVfxEffect effect = effectChain.get(i);
                effect.render(context, pingPongBuffer);
                if (i < count - 1) {
                    pingPongBuffer.swap();
                }
            }
            pingPongBuffer.end();

            // Ensure default texture unit #0 is active.
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0); //TODO Do we need this?

            if (blendingEnabled) {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
        }
        applyingEffects = false;
    }

    public void renderToScreen() {
        if (capturing) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasCaptured) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToScreen(pingPongBuffer.getDstBuffer());
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    public void renderToScreen(int x, int y, int width, int height) {
        if (capturing) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasCaptured) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToScreen(pingPongBuffer.getDstBuffer(), x, y, width, height);
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    public void renderToFbo(VfxFrameBuffer output) {
        if (capturing) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasCaptured) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToFbo(pingPongBuffer.getDstBuffer(), output);
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    private Array<ChainVfxEffect> updateEnabledEffectList() {
        // Build up active effects
        effectsEnabled.clear();
        for (int i = 0; i < effectsAll.size(); i++) {
            ChainVfxEffect effect = effectsAll.get(i);
            if (!effect.isDisabled()) {
                effectsEnabled.add(effect);
            }
        }
        return effectsEnabled;
    }

}