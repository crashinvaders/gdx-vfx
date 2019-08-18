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
import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.utils.PrioritizedArray;
import com.crashinvaders.vfx.gl.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.framebuffer.PingPongBuffer;

/**
 * Provides a way to beginCapture the rendered scene to an off-screen buffer and to apply a chain of effects on it before rendering to
 * screen.
 * <p>
 * Effects can be added or removed via {@link #addEffect(VfxEffect)} and {@link #removeEffect(VfxEffect)}.
 *
 * @author bmanuel
 * @author metaphore
 */
public final class VfxManager implements Disposable {

    private final PrioritizedArray<VfxEffect> effectsAll = new PrioritizedArray<>();
    /** Maintains a per-frame updated list of enabled effects */
    private final Array<VfxEffect> effectsEnabled = new Array<>();

    /** A mesh that is shared among basic filters to draw to full screen. */
    private final ScreenQuadMesh screenQuadMesh = new ScreenQuadMesh();

    private final Format fboFormat;
    private final PingPongBuffer compositeBuffer;

    private final Color clearColor = new Color(Color.CLEAR);
    private boolean cleanUpBuffers = true;

    private boolean disabled = false;
    private boolean capturing = false;
    private boolean hasCaptured = false;
    private boolean rendering = false;

    private boolean blendingEnabled = false;

    private int width, height;

    public VfxManager(Format fboFormat) {
        this(fboFormat, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public VfxManager(Format fboFormat, int bufferWidth, int bufferHeight) {
        this.fboFormat = fboFormat;
        this.compositeBuffer = new PingPongBuffer(fboFormat, bufferWidth, bufferHeight);
        this.width = bufferWidth;
        this.height = bufferHeight;
    }

    @Override
    public void dispose() {
        compositeBuffer.dispose();
        screenQuadMesh.dispose();
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        compositeBuffer.resize(width, height);

        for (int i = 0; i < effectsAll.size(); i++) {
            effectsAll.get(i).resize(width, height);
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    /** Sets whether or not the post-processor should be disabled */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /** @see #setCleanUpBuffers(boolean) */
    public boolean isCleanUpBuffers() {
        return cleanUpBuffers;
    }

    /**
     * Configures off-screen cleanup behavior.
     * If enabled, the off-screen buffers will be cleaned up prior to capturing stage.
     * Enabled by default.
     * @param cleanUpBuffers Whether the buffers should be cleaned up.
     */
    public void setCleanUpBuffers(boolean cleanUpBuffers) {
        this.cleanUpBuffers = cleanUpBuffers;
    }

    public Color getClearColor() {
        return clearColor;
    }

    /** Sets the color that will be used to clean up the off-screen buffers. */
    public void setClearColor(Color color) {
        clearColor.set(color);
    }

    /** Sets the color that will be used to clean up the off-screen buffers. */
    public void setClearColor(int color) {
        clearColor.set(color);
    }

    /** Sets the color that will be used to clean up the off-screen buffers. */
    public void setClearColor(float r, float g, float b, float a) {
        clearColor.set(r, g, b, a);
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
     * Returns the internal framebuffer format, computed from the parameters specified during construction. NOTE: the returned
     * Format will be valid after construction and NOT early!
     */
    public Format getFramebufferFormat() {
        return fboFormat;
    }

    public void setBufferTextureParams(TextureWrap u, TextureWrap v, Texture.TextureFilter min, Texture.TextureFilter mag) {
        compositeBuffer.setTextureParams(u, v, min, mag);
    }

    public boolean isCapturing() {
        return capturing;
    }

    public boolean isRendering() {
        return rendering;
    }

    /**
     * Returns the last active composite buffer.
     */
    public VfxFrameBuffer getResultBuffer() {
        return compositeBuffer.getDstBuffer();
    }

    /**
     * Adds an effect to the effect chain and transfers ownership to the VfxManager.
     * The order of the inserted effects IS important, since effects will be applied in a FIFO fashion,
     * the first added is the first being applied.
     * <p>
     * For more control over the order supply the effect with a priority - {@link #addEffect(VfxEffect, int)}.
     * @see #addEffect(VfxEffect, int)
     */
    public void addEffect(VfxEffect effect) {
        addEffect(effect, 0);
    }

    public void addEffect(VfxEffect effect, int priority) {
        effectsAll.add(effect, priority);
        effect.resize(width, height);
    }

    /** Removes the specified effect from the effect chain. */
    public void removeEffect(VfxEffect effect) {
        effectsAll.remove(effect);
    }

    public void removeAllEffects() {
        effectsAll.clear();
    }

    public void setEffectPriority(VfxEffect effect, int priority) {
        effectsAll.setPriority(effect, priority);
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

        if (disabled || capturing) return false;

        // Check if any effects are enabled.
        if (!checkForAnyActiveEffect()) return false;

        capturing = true;

        compositeBuffer.begin();

        if (cleanUpBuffers) {
            compositeBuffer.cleanUpBuffers(clearColor);
        }

        return true;
    }

    /**
     * Stops capturing the scene.
     * @return false if there was no capturing before that call.
     */
    public boolean endCapture() {
        if (disabled || !capturing) return false;

        capturing = false;
        hasCaptured = true;
        compositeBuffer.end();
        return true;
    }

    /**
     * Convenience method to render to the screen.
     * @see #render(VfxFrameBuffer)
     **/
    public void render() {
        render(null);
    }

    /**
     * Stops capturing the scene and apply the effect chain, if there is one.
     * @param dst Target frame buffer, where result will be rendered to.
     *             If null, rendering will be performed to the screen.
     */
    public void render(VfxFrameBuffer dst) {
        if (capturing) {
            throw new IllegalStateException("You should call VfxManager.endCapture() prior effect rendering.");
        }

        if (disabled) return;
        if (!hasCaptured) return;

        updateEnabledEffectList();
        Array<VfxEffect> items = effectsEnabled;

        rendering = true;
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
                compositeBuffer.swap(); // Swap buffers to get captured result in src buffer.
                compositeBuffer.begin();
                for (int i = 0; i < count - 1; i++) {
                    VfxEffect effect = items.get(i);
                    effect.render(screenQuadMesh,
                            compositeBuffer.getSrcBuffer(),
                            compositeBuffer.getDstBuffer());
                    if (i < count - 2) {
                        compositeBuffer.swap();
                    }
                }
                compositeBuffer.end();
            }

            // Render with null dest (to screen).
            items.get(count - 1).render(screenQuadMesh, compositeBuffer.getDstBuffer(), dst);

            // Ensure default texture unit #0 is active.
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

            if (blendingEnabled) {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }
        }
        rendering = false;
    }

    private boolean checkForAnyActiveEffect() {
        for (int i = 0; i < effectsAll.size(); i++) {
            if (!effectsAll.get(i).isDisabled()) {
                return true;
            }
        }
        return false;
    }

    private int updateEnabledEffectList() {
        // Build up active effects
        effectsEnabled.clear();
        for (int i = 0; i < effectsAll.size(); i++) {
            VfxEffect effect = effectsAll.get(i);
            if (!effect.isDisabled()) {
                effectsEnabled.add(effect);
            }
        }
        return effectsEnabled.size;
    }
}
