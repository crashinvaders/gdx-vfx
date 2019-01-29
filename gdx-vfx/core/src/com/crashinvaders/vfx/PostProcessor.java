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

package com.crashinvaders.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.common.PrioritizedArray;
import com.crashinvaders.common.framebuffer.FboWrapper;
import com.crashinvaders.common.framebuffer.PingPongBuffer;

/**
 * Provides a way to beginCapture the rendered scene to an off-screen buffer and to apply a chain of effects on it before rendering to
 * screen.
 * <p>
 * Effects can be added or removed via {@link #addEffect(PostProcessorEffect)} and {@link #removeEffect(PostProcessorEffect)}.
 *
 * @author bmanuel
 * @author metaphore
 */
public final class PostProcessor implements Disposable {

    private final PrioritizedArray<PostProcessorEffect> effectsAll = new PrioritizedArray<>();
    /** Maintains a per-frame updated list of enabled effects */
    private final Array<PostProcessorEffect> effectsEnabled = new Array<>();
    private final Array<PostProcessorEffect> effectsToRemove = new Array<>();

    private final Color clearColor = new Color(Color.CLEAR);
    private final Format fboFormat;
    private final PingPongBuffer composite;

    private boolean enabled = true;
    private boolean capturing = false;
    private boolean hasCaptured = false;

    private PostProcessorListener listener = null;
    private boolean cleanUpBuffers = true;
    private boolean blendingEnabled = false;

    public PostProcessor(Format fboFormat) {
        this.fboFormat = fboFormat;
        composite = new PingPongBuffer(fboFormat);
    }

    @Override
    public void dispose() {
        composite.dispose();
    }

    public void resize(int width, int height) {
        composite.resize(width, height);

        for (PostProcessorEffect effect : effectsAll) {
            effect.resize(width, height);
        }
    }

    /** Whether or not the post-processor is enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /** If called before capturing it will indicate if the next capture call will succeeds or not. */
    public boolean isReady() {
        boolean hasEffects = false;

        for (PostProcessorEffect e : effectsAll) {
            if (e.isEnabled()) {
                hasEffects = true;
                break;
            }
        }

        return (enabled && !capturing && hasEffects);
    }

    /** Sets whether or not the post-processor should be enabled */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Returns the number of the currently enabled effects */
    public int getEnabledEffectsCount() {
        return effectsEnabled.size;
    }

    /** Sets the listener that will receive events triggered by the PostProcessor rendering pipeline. */
    public void setListener(PostProcessorListener listener) {
        this.listener = listener;
    }

    public boolean isCleanUpBuffers() {
        return cleanUpBuffers;
    }

    public void setCleanUpBuffers(boolean cleanUpBuffers) {
        this.cleanUpBuffers = cleanUpBuffers;
    }
    public boolean isBlendingEnabled() {
        return blendingEnabled;
    }

    /** Enables OpenGL blending for the effect chain rendering stage. */
    public void setBlendingEnabled(boolean blendingEnabled) {
        this.blendingEnabled = blendingEnabled;
    }

    /**
     * Adds the specified effect to the effect chain and transfer ownership to the PostProcessor, it will manage cleaning it up for
     * you. The order of the inserted effects IS important, since effects will be applied in a FIFO fashion, the first added is the
     * first being applied.
     */
    public void addEffect(PostProcessorEffect effect) {
        addEffect(effect, 0);
    }

    public void addEffect(PostProcessorEffect effect, int priority) {
        effect.resize(composite.buffer1.getFbo().getWidth(), composite.buffer1.getFbo().getHeight());
        effectsAll.add(effect, priority);
    }

    /** Removes the specified effect from the effect chain. */
    public void removeEffect(PostProcessorEffect effect) {
        if (isCapturing()) {
            // Post removal
            effectsToRemove.add(effect);
        } else {
            effectsAll.remove(effect);
        }
    }

    public void removeAllEffects() {
        if (isCapturing()) {
            for (int i = 0; i < effectsAll.size(); i++) {
                effectsToRemove.add(effectsAll.get(i));
            }
        } else {
            effectsAll.clear();
        }
    }

    /**
     * Returns the internal framebuffer format, computed from the parameters specified during construction. NOTE: the returned
     * Format will be valid after construction and NOT early!
     */
    public Format getFramebufferFormat() {
        return fboFormat;
    }

    /** Sets the color that will be used to clear the buffer. */
    public void setClearColor(Color color) {
        clearColor.set(color);
    }

    /** Sets the color that will be used to clear the buffer. */
    public void setClearColor(int color) {
        clearColor.set(color);
    }

    /** Sets the color that will be used to clear the buffer. */
    public void setClearColor(float r, float g, float b, float a) {
        clearColor.set(r, g, b, a);
    }

    public void setBufferTextureParams(TextureWrap u, TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        composite.setTextureParams(u, v, min, mag);
    }

    public boolean isCapturing() {
        return capturing;
    }

    /**
     * Starts capturing the scene.
     * If {@link #cleanUpBuffers} is enabled,
     * the off-screen buffers will be cleaned up with {@link #clearColor}.
     *
     * @return true or false, whether or not capturing has been initiated. Capturing will fail in case there are no enabled effects
     * in the chain or this instance is not enabled or capturing is already started.
     */
    public boolean beginCapture() {
        hasCaptured = false;

        if (!enabled || capturing) return false;

        // Check if any effects are enabled.
        if (buildEnabledEffectsList() == 0) return false;

        capturing = true;

        if (cleanUpBuffers) {
            composite.begin();
            composite.capture();
            Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            composite.capture();
            Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        } else {
            composite.begin();
            composite.capture();
        }
        return true;

    }

    /** Stops capturing the scene and returns the result, or null if nothing was captured. */
    public FboWrapper endCapture() {
        if (!enabled || !capturing) return null;

        capturing = false;
        hasCaptured = true;
        composite.end();
        return composite.getResultBuffer();
    }

    public PingPongBuffer getCombinedBuffer() {
        return composite;
    }

    /** After a {@link #beginCapture()}/{@link #endCapture()} action, returns the just captured buffer */
    public FboWrapper getCapturedBuffer() {
        if (enabled && hasCaptured) {
            return composite.getResultBuffer();
        }
        return null;
    }

    /** Regenerates and/or rebinds owned resources when needed, eg. when the OpenGL context is lost. */
    public void rebind() {
        composite.rebind();

        for (PostProcessorEffect e : effectsAll) {
            e.rebind();
        }
    }

    /** Convenience method to render to screen. */
    public void render() {
        render(null);
    }

    /**
     * Stops capturing the scene and apply the effect chain, if there is one. If the specified output framebuffer is NULL, then the
     * rendering will be performed to screen.
     */
    public void render(FboWrapper dest) {
        endCapture();

        if (!hasCaptured) {
            return;
        }

        Array<PostProcessorEffect> items = effectsEnabled;

        int count = items.size;
        if (count > 0) {

            // Enable blending to preserve buffer's alpha values.
            if (blendingEnabled) {
                Gdx.gl.glEnable(GL20.GL_BLEND);
            }

            Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

            // Render effects chain, [0,n-1].
            if (count > 1) {
                for (int i = 0; i < count - 1; i++) {
                    PostProcessorEffect effect = items.get(i);
                    composite.capture();
                    effect.render(composite.getSourceBuffer(), composite.getResultBuffer());
                }
                composite.end();
            }

            if (listener != null && dest == null) {
                listener.beforeRenderToScreen();
            }

            // Render with null dest (to screen).
            items.get(count - 1).render(composite.getResultBuffer(), dest);

            // Ensure default texture unit #0 is active.
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

            if (blendingEnabled) {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
        } else {
            Gdx.app.log("PostProcessor", "No post-processor effects enabled, aborting render");
        }

    }

    private int buildEnabledEffectsList() {
        // Remove pending effects.
        for (int i = 0; i < effectsToRemove.size; i++) {
            PostProcessorEffect effect = effectsToRemove.get(i);
            effectsAll.remove(effect);
        }
        effectsToRemove.clear();

        // Build up active effects
        effectsEnabled.clear();
        for (int i = 0; i < effectsAll.size(); i++) {
            PostProcessorEffect effect = effectsAll.get(i);
            if (effect.isEnabled()) {
                effectsEnabled.add(effect);
            }
        }
        return effectsEnabled.size;
    }
}
