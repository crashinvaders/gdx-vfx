package com.crashinvaders.common.framebuffer;

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
import com.crashinvaders.common.gl.GLExtCalls;
import com.crashinvaders.common.gl.GLUtils;

/**
 * Wraps {@link FrameBuffer} and manages current OpenGL frame buffer.
 * So you can use multiple instances of this class to drawToScreen into one framebuffer while you drawing into another one.
 * <p>
 * {@link FboWrapper} internally switches GL viewport and between {@link #begin()} and {@link #end()}.
 * If you are using any kind of batch renders (e.g. {@link Batch} or {@link ShapeRenderer}),
 * then you are probably interested in updating their transform and projection matrices.
 * You can do this by registering {@link Renderer} using {@link #addRenderer(Renderer)} and {@link #removeRenderer(Renderer)}.
 * Registered renderers will be automatically switch their matrices back and forth prior to {@link #begin()} and {@link #end()} calls.
 * They will also be flushed in the right time.
 * <p/>
 * <b>NOTE:</b> Depth and stencil buffers are not supported (yet?)
 */
public class FboWrapper implements Disposable {
    private static final OrthographicCamera tmpCam = new OrthographicCamera();
    private static final Matrix4 zeroTransform = new Matrix4();

    public static int bufferNesting = 0;

    private final Matrix4 localProjection = new Matrix4();
    private final Matrix4 localTransform = new Matrix4();

    private final RendererManager renderers = new RendererManager();

    private final GLExtCalls.Viewport preservedViewport = new GLExtCalls.Viewport();
    private final Pixmap.Format pixelFormat;
    private int preservedFboHandle;

    private FrameBuffer fbo;
    private boolean initialized;
    private boolean drawing;

    public FboWrapper(Pixmap.Format pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    public FrameBuffer getFbo() {
        return fbo;
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

    @Override
    public void dispose() {
        if (!initialized) return;

        initialized = false;

        fbo.dispose();
        fbo = null;
    }

    public Pixmap.Format getPixelFormat() {
        return pixelFormat;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /** @return true means that {@link FboWrapper#begin()} was called */
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

//        System.out.println("FboWrapper.begin " + fbo.getFramebufferHandle());
        if (!initialized) throw new IllegalStateException("BatchedFboWrapper must be initialized first");
        if (drawing) throw new IllegalStateException("Already drawing");

        drawing = true;

        renderers.flush();
        preservedFboHandle = getBoundFboHandle();
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());

        preservedViewport.set(getViewport());
        Gdx.gl20.glViewport(0, 0, getFbo().getWidth(), getFbo().getHeight());
        renderers.assignLocalMatrices(localProjection, localTransform);
    }

    public void end() {
        bufferNesting--;

//        System.out.println("FboWrapper.end " + fbo.getFramebufferHandle());
        if (!initialized) throw new IllegalStateException("BatchedFboWrapper must be initialized first");
        if (!drawing) throw new IllegalStateException("Is not drawing");

        if (getBoundFboHandle() != fbo.getFramebufferHandle()) {
            throw new IllegalStateException("Current bound OpenGL FBO's handle doesn't match to wrapped one. It seems like begin/end order was violated.");
        }

        drawing = false;

        renderers.flush();
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, preservedFboHandle);
        Gdx.gl20.glViewport(preservedViewport.x, preservedViewport.y, preservedViewport.width, preservedViewport.height);
        renderers.restoreOwnMatrices();
    }

    protected int getBoundFboHandle() {
        int boundFboHandle = GLUtils.getBoundFboHandle();
//        Gdx.app.log("FboWrapper", "Bound frame buffer handle is " + String.valueOf(boundFboHandle));
        return boundFboHandle;

//        IntBuffer intBuf = tmpIntBuf;
//        Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, intBuf);
//        return intBuf.get(0);
    }

    protected GLExtCalls.Viewport getViewport() {
        GLExtCalls.Viewport viewport = GLUtils.getViewport();
//        Gdx.app.log("FboWrapper", "Current viewport is " + viewport);
        return viewport;

//        IntBuffer intBuf = tmpIntBuf;
//        Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf);
//        return tmpViewport.set(intBuf.get(0), intBuf.get(1), intBuf.get(2), intBuf.get(3));
    }

    public static class RendererManager implements Renderer {

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

        //region Renderer implementation
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
        //endregion
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
            setTransform(transform);
        }

        @Override
        public void restoreOwnMatrices() {
            setProjection(preservedProjection);
            setTransform(preservedTransform);
        }

        protected abstract Matrix4 getProjection();
        protected abstract Matrix4 getTransform();
        protected abstract void setProjection(Matrix4 projection);
        protected abstract void setTransform(Matrix4 transform);
    }

    public static class BatchRenderAdapter extends RendererAdapter implements Pool.Poolable {
        private Batch batch;

        public BatchRenderAdapter() {
        }

        public BatchRenderAdapter initialize(Batch batch) {
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

    public static class ShapesRenderAdapter extends RendererAdapter implements Pool.Poolable {
        private ShapeRenderer shapeRenderer;

        public ShapesRenderAdapter() {
        }

        public ShapesRenderAdapter initialize(ShapeRenderer shapeRenderer) {
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