
package com.crashinvaders.vfx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.ByteBuffer;

public final class PostProcessorUtils {
    /** Enable pipeline state queries: beware the pipeline can stall! */
    public static boolean enableQueryStates = false;

    private static final ByteBuffer byteBuffer = BufferUtils.newByteBuffer(32);

    /**
     * Provides a simple mechanism to query OpenGL pipeline states.
     * Note: state queries are costly and stall the pipeline, especially on mobile devices!
     * <br/>
     * Queries switched off by default. Update {@link #enableQueryStates} flag to enable them.
     */
    public static boolean isGlEnabled(int pName) {
        if (!enableQueryStates) return false;

        boolean result;

        switch (pName) {
            case GL20.GL_BLEND:
                Gdx.gl20.glGetBooleanv(GL20.GL_BLEND, byteBuffer);
                result = (byteBuffer.get() == 1);
                byteBuffer.clear();
                break;
            default:
                result = false;
        }

        return result;
    }
}
