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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.crashinvaders.vfx.gl.VfxGlViewport;

/**
 * Wraps {@link FrameBuffer} and manages currently bound OpenGL FBO.
 * <p>
 * This implementation supports nested frame buffer drawing approach.
 * You can use multiple instances of this class to draw into one frame buffer while you drawing into another one,
 * the OpenGL state will be managed properly.
 * <br>
 * Here's an example:
 * <pre>
 * FboWrapper buffer0, buffer1;
 * // ...
 * void render() {
 *      // Any drawing here will be performed directly to the screen.
 *      buffer0.begin();
 *      // Any drawing here will be performed into buffer0's FBO.
 *      buffer1.begin();
 *      // Any drawing here will be performed into buffer1's FBO.
 *      buffer1.end();
 *      // Any drawing here will be performed into buffer0's FBO.
 *      buffer0.end();
 *      // Any drawing here will be performed directly to the screen.
 * }
 * </pre>
 * <p>
 * {@link VfxFrameBuffer} internally switches GL viewport between {@link #begin()} and {@link #end()}.
 * <br>
 * If you use any kind of batch renders (e.g. {@link Batch} or {@link ShapeRenderer}),
 * you should update their transform and projection matrices to setup viewport to the target frame buffer's size.
 * You can do so by registering {@link Renderer} using {@link #addRenderer(Renderer)} and {@link #removeRenderer(Renderer)}.
 * The registered renderers will automatically switch their matrices back and forth respectively upon {@link #begin()} and {@link #end()} calls.
 * They will also be flushed in the right time.
 * <p>
 * <b>NOTE:</b> Depth and stencil buffers are not supported.
 *
 * @author metaphore
 */
public class VfxFrameBuffer implements Disposable {
    /** Current depth of buffer nesting rendering (keeps track of how many buffers are currently activated). */
    private static int bufferNesting = 0;
    /** @see #bufferNesting */
    public static int getBufferNesting() { return bufferNesting; }

    private static final OrthographicCamera tmpCam = new OrthographicCamera();
    private static final Matrix4 zeroTransform = new Matrix4();

    private final Matrix4 localProjection = new Matrix4();
    private final Matrix4 localTransform = new Matrix4();

    private final RendererManager renderers = new RendererManager();

    private final VfxGlViewport preservedViewport = new VfxGlViewport();
    private final Pixmap.Format pixelFormat;    //TODO Shall be non-final and become a parameter of #initialize().
    private int previousFboHandle;

    private FrameBuffer fbo = null;
    private boolean initialized;
    private boolean drawing;

    public VfxFrameBuffer(Pixmap.Format pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void dispose() {
        reset();
    }

    public void initialize(int width, int height) {
        if (initialized) { dispose(); }

        initialized = true;

        int boundFboHandle = getBoundFboHandle();
        fbo = new FrameBuffer(pixelFormat, width, height, false);
        fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, boundFboHandle);

        OrthographicCamera cam = tmpCam;
        cam.setToOrtho(false, width, height);
        localProjection.set(cam.combined);
        localTransform.set(zeroTransform);
    }

    public void reset() {
        if (!initialized) return;

        initialized = false;

        fbo.dispose();
        fbo = null;
    }

    public FrameBuffer getFbo() {
        return fbo;
    }

    public Texture getTexture() {
        return fbo == null ? null : fbo.getColorBufferTexture();
    }

    public Pixmap.Format getPixelFormat() {
        return pixelFormat;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /** @return true means {@link VfxFrameBuffer#begin()} has been called */
    public boolean isDrawing() {
        return drawing;
    }

    public void addRenderer(Renderer renderer) {
        renderers.addRenderer(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        renderers.removeRenderer(renderer);
    }

    public void clearRenderers() {
        renderers.clearRenderers();
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        localProjection.set(matrix);
    }

    public void setTransformMatrix(Matrix4 matrix) {
        localTransform.set(matrix);
    }

    public Matrix4 getProjectionMatrix() {
        return localProjection;
    }

    public Matrix4 getTransformMatrix() {
        return localTransform;
    }

    public void begin() {
        bufferNesting++;

        if (!initialized) throw new IllegalStateException("VfxFrameBuffer must be initialized first");
        if (drawing) throw new IllegalStateException("Already drawing");

        drawing = true;

        renderers.flush();
        previousFboHandle = getBoundFboHandle();
        preservedViewport.set(getViewport());
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl20.glViewport(0, 0, getFbo().getWidth(), getFbo().getHeight());
        renderers.assignLocalMatrices(localProjection, localTransform);
    }

    public void end() {
        bufferNesting--;

        if (!initialized) throw new IllegalStateException("VfxFrameBuffer must be initialized first");
        if (!drawing) throw new IllegalStateException("Is not drawing");

        if (getBoundFboHandle() != fbo.getFramebufferHandle()) {
            throw new IllegalStateException("Current bound OpenGL FBO's handle doesn't match to wrapped one. It seems like begin/end order was violated.");
        }

        drawing = false;

        renderers.flush();
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, previousFboHandle);
        Gdx.gl20.glViewport(preservedViewport.x, preservedViewport.y, preservedViewport.width, preservedViewport.height);
        renderers.restoreOwnMatrices();
    }

    protected int getBoundFboHandle() {
        int boundFboHandle = VfxGLUtils.getBoundFboHandle();
        return boundFboHandle;
    }

    protected VfxGlViewport getViewport() {
        VfxGlViewport viewport = VfxGLUtils.getViewport();
        return viewport;
    }

    private static class RendererManager implements Renderer {

        private final Array<Renderer> renderers = new Array<>();

        // Closed CTOR
        RendererManager() { }

        public void addRenderer(Renderer renderer) {
            renderers.add(renderer);
        }

        public void removeRenderer(Renderer renderer) {
            renderers.removeValue(renderer, true);
        }

        public void clearRenderers() {
            renderers.clear();
        }

        @Override
        public void flush() {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).flush();
            }
        }
        @Override
        public void assignLocalMatrices(Matrix4 projection, Matrix4 transform) {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).assignLocalMatrices(projection, transform);
            }
        }
        @Override
        public void restoreOwnMatrices() {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).restoreOwnMatrices();
            }
        }
    }

    public interface Renderer {
        void flush();
        void assignLocalMatrices(Matrix4 projection, Matrix4 transform);
        void restoreOwnMatrices();
    }

    public static abstract class RendererAdapter implements Renderer {
        private final Matrix4 preservedProjection = new Matrix4();
        private final Matrix4 preservedTransform = new Matrix4();

        @Override
        public void assignLocalMatrices(Matrix4 projection, Matrix4 transform) {
            preservedProjection.set(getProjection());
            preservedTransform.set(getTransform());
            setProjection(projection);
//            setTransform(transform);
        }

        @Override
        public void restoreOwnMatrices() {
            setProjection(preservedProjection);
//            setTransform(preservedTransform);
        }

        protected abstract Matrix4 getProjection();
        protected abstract Matrix4 getTransform();
        protected abstract void setProjection(Matrix4 projection);
        protected abstract void setTransform(Matrix4 transform);
    }

    public static class BatchRendererAdapter extends RendererAdapter implements Pool.Poolable {
        private Batch batch;

        public BatchRendererAdapter() {
        }

        public BatchRendererAdapter(Batch batch) {
            initialize(batch);
        }

        public BatchRendererAdapter initialize(Batch batch) {
            this.batch = batch;
            return this;
        }

        @Override
        public void reset() {
            batch = null;
        }

        public Batch getBatch() {
            return batch;
        }

        @Override
        public void flush() {
            batch.isDrawing(); {
                batch.flush();
            }
        }
        @Override
        protected Matrix4 getProjection() {
            return batch.getProjectionMatrix();
        }
        @Override
        protected Matrix4 getTransform() {
            return batch.getTransformMatrix();
        }
        @Override
        protected void setProjection(Matrix4 projection) {
            batch.setProjectionMatrix(projection);
        }
        @Override
        protected void setTransform(Matrix4 transform) {
            batch.setTransformMatrix(transform);
        }
    }

    public static class ShapeRendererAdapter extends RendererAdapter implements Pool.Poolable {
        private ShapeRenderer shapeRenderer;

        public ShapeRendererAdapter() {
        }

        public ShapeRendererAdapter(ShapeRenderer shapeRenderer) {
            initialize(shapeRenderer);
        }

        public ShapeRendererAdapter initialize(ShapeRenderer shapeRenderer) {
            this.shapeRenderer = shapeRenderer;
            return this;
        }

        @Override
        public void reset() {
            shapeRenderer = null;
        }

        public ShapeRenderer getShapeRenderer() {
            return shapeRenderer;
        }

        @Override
        public void flush() {
            if (shapeRenderer.isDrawing()) {
                shapeRenderer.flush();
            }
        }
        @Override
        protected Matrix4 getProjection() {
            return shapeRenderer.getProjectionMatrix();
        }
        @Override
        protected Matrix4 getTransform() {
            return shapeRenderer.getTransformMatrix();
        }
        @Override
        protected void setProjection(Matrix4 projection) {
            shapeRenderer.setProjectionMatrix(projection);
        }
        @Override
        protected void setTransform(Matrix4 transform) {
            shapeRenderer.setTransformMatrix(transform);
        }
    }
}