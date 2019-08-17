package com.crashinvaders.vfx;

import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.gl.ScreenQuadMesh;
import com.crashinvaders.vfx.gl.framebuffer.FboWrapper;

/**
 * This interface defines the base class for the concrete implementation of post-processor effects.
 * An effect is considered enabled by default.
 *
 * @author bmanuel
 * @author metaphore
 */
public abstract class PostProcessorEffect implements Disposable {

    protected boolean disabled = false;

    /**
     * This method will be called once effect will be added to {@link PostProcessorManager}.
     * Also it will be called on every application resize as usual.
     */
    public abstract void resize(int width, int height);

    /**
     * Concrete objects shall be responsible to recreate or rebind its own resources whenever its needed, usually when the OpenGL
     * context is lost. Eg., framebuffer textures should be updated and shader parameters should be reuploaded/rebound.
     */
    public abstract void rebind();

    /** Concrete objects shall implements its own rendering, given the source and destination buffers. */
    public abstract void render(ScreenQuadMesh mesh, final FboWrapper src, final FboWrapper dst);

    /** Whether or not this effect is disabled and shouldn't be processed */
    public boolean isDisabled() {
        return disabled;
    }

    /** Sets this effect disabled or not */
    public void setDisabled(boolean enabled) {
        this.disabled = enabled;
    }

}
