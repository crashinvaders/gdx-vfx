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
    private final Array<ChainVfxEffect> effectsEnabled = new Array<>(); //TODO Get rid of this.
    private final VfxRenderContext context;
    private final VfxPingPongWrapper pingPongWrapper;
    private final ScreenCaptureHelper captureHelper;

    private boolean disabled = false; //TODO Remove the property.
//    private boolean capturing = false;  //TODO Remove the property.

    private boolean hasInputScene = false;
    private boolean hasProcessedScene = false;

    private boolean applyingEffects = false;

    private boolean blendingEnabled = false;

    private int width, height;

    public VfxManager(Format fboFormat) {
        this(fboFormat, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
    }

    public VfxManager(Format fboFormat, int bufferWidth, int bufferHeight) {
        this.width = bufferWidth;
        this.height = bufferHeight;

        this.context = new VfxRenderContext(fboFormat, bufferWidth, bufferHeight);

        // VfxFrameBufferPool will manage both ping-pong VfxFrameBuffer instances for us.
        this.pingPongWrapper = new VfxPingPongWrapper(context.getBufferPool());

        captureHelper = new ScreenCaptureHelper();
    }

    @Override
    public void dispose() {
        pingPongWrapper.reset();
        context.dispose();
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
        Texture srcTexture = pingPongWrapper.getSrcTexture();
        srcTexture.setWrap(u, v);
        srcTexture.setFilter(min, mag);
        Texture dstTexture = pingPongWrapper.getSrcTexture();
        dstTexture.setWrap(u, v);
        dstTexture.setFilter(min, mag);
    }

//    public boolean isCapturing() {
//        return capturing;
//    }

    public boolean isApplyingEffects() {
        return applyingEffects;
    }

    public boolean hasProcessedScene() {
        return hasProcessedScene;
    }

    /** @return the last active destination frame buffer. */
    public VfxFrameBuffer getResultBuffer() {
        return pingPongWrapper.getDstBuffer();
    }

    /** @return the internal ping-pong buffer. */
    public VfxPingPongWrapper getPingPongWrapper() {
        return pingPongWrapper;
    }

    public VfxRenderContext getRenderContext() {
        return context;
    }

    public ScreenCaptureHelper getCaptureHelper() {
        return captureHelper;
    }

    /**
     * Adds an effect to the effect chain and transfers ownership to the VfxManager.
     * The order of the inserted effects IS important, since effects will be applied in a FIFO fashion,
     * the first added is the first being applied.
     * <p>
     * For more control over the order supply the effect with a priority - {@link #addEffect(ChainVfxEffect, int)}.
     * @see #addEffect(ChainVfxEffect, int)
     */
    public void addEffect(ChainVfxEffect effect) {
        addEffect(effect, 0);
    }

    public void addEffect(ChainVfxEffect effect, int priority) {
        effectsAll.add(effect, priority);
        effect.resize(width, height);
    }

    /** Removes the specified effect from the effect chain. */
    public void removeEffect(ChainVfxEffect effect) {
        effectsAll.remove(effect);
    }

    /** Removes all effects from the effect chain. */
    public void removeAllEffects() {
        effectsAll.clear();
    }

    /** Changes the order of the effect in the effect chain. */
    public void setEffectPriority(ChainVfxEffect effect, int priority) {
        effectsAll.setPriority(effect, priority);
    }

    /** Cleans up the {@link VfxPingPongWrapper}'s buffers with {@link Color#CLEAR}. */
    public void cleanUpBuffers() {
        cleanUpBuffers(Color.CLEAR);
    }

    /** Cleans up the {@link VfxPingPongWrapper}'s buffers with the color specified. */
    public void cleanUpBuffers(Color color) {
        if (applyingEffects) throw new IllegalStateException("Cannot clean up buffers when applying effects.");
        if (captureHelper.isCapturing()) throw new IllegalStateException("Cannot clean up buffers when capturing a scene.");

        pingPongWrapper.cleanUpBuffers(color);

        hasProcessedScene = false;
        hasInputScene = false;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        context.resize(width, height);

        for (int i = 0; i < effectsAll.size(); i++) {
            effectsAll.get(i).resize(width, height);
        }
    }

    public void rebind() {
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

//    /**
//     * Starts capturing the scene.
//     *
//     * @return true or false, whether or not capturing has been initiated.
//     * Capturing will fail if the manager is disabled or capturing is already started.
//     */
//    public boolean beginCapture() {
//        if (applyingEffects) {
//            throw new IllegalStateException("You cannot capture when you're applying the effects.");
//        }
//
//        if (disabled) return false;
//        if (capturing) return false;
//
//        capturing = true;
//        pingPongWrapper.begin();
//        return true;
//    }
//
//    /**
//     * Stops capturing the scene.
//     * @return false if there was no capturing before that call.
//     */
//    public boolean endCapture() {
//        if (!capturing) throw new IllegalStateException("The capturing is not started. Forgot to call beginCapture()?");
//
//        hasCaptured = true;
//        capturing = false;
//        pingPongWrapper.end();
//        return true;
//    }

    /** @see VfxManager#useAsInputScene(Texture)  */
    public void useAsInputScene(VfxFrameBuffer frameBuffer) {
        useAsInputScene(frameBuffer.getTexture());
    }

    /** Sets up a (captured?) source scene that will be used later as an input for effect processing.
     * Updates the effect chain src buffer with the data provided. */
    public void useAsInputScene(Texture texture) {
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("Cannot set captured input when capture helper is currently capturing.");
        }

        context.getBufferRenderer().renderToFbo(texture, pingPongWrapper.getDstBuffer());

        hasInputScene = true;
        hasProcessedScene = false;
    }

    public void useAsInputScene(ScreenCaptureHelper captureHelper) {
        if (captureHelper != this.captureHelper) {
            throw new IllegalStateException("Can only use capture helper obtained though VfxManager#getCaptureHelper() method of this instance.");
        }
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("Capture helper is in capturing stage. Forgot to call CaptureHelper#endCapture() ?");
        }
        if (!captureHelper.isResultCaptured()) {
            throw new IllegalStateException("Capture helper doesn't have a captured result. " +
                    "You should call CaptureHelper#startCapture() and CaptureHelper#endCapture() " +
                    "before you can provide the capture helper as an input for the effect processing.");
        }

        // Capture helper uses VfxManager#pingPongWrapper#bufDst frame buffer to capture the scene.
        // So we don't need to do anything else to use it as an input.
        hasInputScene = true;
        hasProcessedScene = false;
        captureHelper.resetState();
    }

    /** Applies the effect chain, if there is one. */
    public void applyEffects() {
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before applying the effects.");
        }

        if (disabled) return;
        if (!hasInputScene) return;

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
            pingPongWrapper.swap(); // Swap buffers to get the input scene in the src buffer.
            pingPongWrapper.begin();
            for (int i = 0; i < count; i++) {
                ChainVfxEffect effect = effectChain.get(i);
                effect.render(context, pingPongWrapper);
                if (i < count - 1) {
                    pingPongWrapper.swap();
                }
            }
            pingPongWrapper.end();

            // Ensure default texture unit #0 is active.
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

            if (blendingEnabled) {
                Gdx.gl.glDisable(GL20.GL_BLEND);
            }

            hasProcessedScene = true;
        }
        applyingEffects = false;
    }

    public void renderToScreen() {
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("You should call endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasInputScene) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToScreen(pingPongWrapper.getDstBuffer());
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    public void renderToScreen(int x, int y, int width, int height) {
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("You should call endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasInputScene) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToScreen(pingPongWrapper.getDstBuffer(), x, y, width, height);
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    public void renderToFbo(VfxFrameBuffer output) {
        if (captureHelper.isCapturing()) {
            throw new IllegalStateException("You should call VfxManager.endCapture() before rendering the result.");
        }
        if (disabled) return;
        if (!hasInputScene) return;

        // Enable blending to preserve buffer's alpha values.
        if (blendingEnabled) { Gdx.gl.glEnable(GL20.GL_BLEND); }
        context.getBufferRenderer().renderToFbo(pingPongWrapper.getDstBuffer(), output);
        if (blendingEnabled) { Gdx.gl.glDisable(GL20.GL_BLEND); }
    }

    //TODO Remove this method and use SnapshotArray instead.
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

    public class ScreenCaptureHelper {

        private boolean capturing = false;
        private boolean resultCaptured = false;

        public boolean isCapturing() {
            return capturing;
        }

        public boolean isResultCaptured() {
            return resultCaptured;
        }

        /**
         * Starts capturing the scene.
         */
        public void beginCapture() {
            if (capturing) return;

            if (applyingEffects) {
                throw new IllegalStateException("You cannot capture when you're applying the effects.");
            }

            capturing = true;
            pingPongWrapper.begin();
        }

        /**
         * Stops capturing the scene.
         */
        public void endCapture() {
            if (!capturing) throw new IllegalStateException("The capturing is not started. Forgot to call beginCapture()?");

            hasInputScene = true;
            capturing = false;
            pingPongWrapper.end();

            resultCaptured = true;
        }

        public VfxFrameBuffer getCapturedResult() {
            return pingPongWrapper.getDstBuffer();
        }

        protected void resetState() {
            if (capturing) {
                throw new IllegalStateException("Cannot reset the state when in capturing state.");
            }
            capturing = false;
            resultCaptured = false;
        }
    }
}