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

package com.crashinvaders.vfx.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;

public class VfxGLUtils {
    private static final String TAG = VfxGLUtils.class.getSimpleName();

    //TODO Remove this after https://github.com/libgdx/libgdx/issues/4688 gets resolved
    // This field may be used to provide custom implementation
    public static VfxGlExtension glExtension = new DefaultVfxGlExtension();

    public static int getBoundFboHandle() {
        return glExtension.getBoundFboHandle();
    }

    public static VfxGlExtension.Viewport getViewport() {
        return glExtension.getViewport();
    }

    public static ShaderProgram compileShader(FileHandle vertexFile, FileHandle fragmentFile) {
        return compileShader(vertexFile, fragmentFile, "");
    }

    public static ShaderProgram compileShader(FileHandle vertexFile, FileHandle fragmentFile, String defines) {
        if (fragmentFile == null) {
            throw new IllegalArgumentException("Vertex shader file cannot be null.");
        }
        if (vertexFile == null) {
            throw new IllegalArgumentException("Fragment shader file cannot be null.");
        }
        if (defines == null) {
            throw new IllegalArgumentException("Defines cannot be null.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Compiling \"").append(vertexFile.name()).append('/').append(fragmentFile.name()).append('\"');
        if (defines.length() > 0) {
            sb.append(" w/ (").append(defines.replace("\n", ", ")).append(")");
        }
        sb.append("...");
        Gdx.app.log(TAG, sb.toString());

        String vpSrc = vertexFile.readString();
        String fpSrc = fragmentFile.readString();

        ShaderProgram shader = new ShaderProgram(defines + "\n" + vpSrc, defines + "\n" + fpSrc);

        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error: " + vertexFile.name() + "/" + fragmentFile.name() + "\n" + shader.getLog());
        }
        return shader;
    }

    //region GL state queries

    /** Enable pipeline state queries: beware the pipeline can stall! */
    public static boolean enableGLQueryStates = false;

    private static final ByteBuffer byteBuffer = BufferUtils.newByteBuffer(32);

    /**
     * Provides a simple mechanism to query OpenGL pipeline states.
     * Note: state queries are costly and stall the pipeline, especially on mobile devices!
     * <br/>
     * Queries switched off by default. Update {@link #enableGLQueryStates} flag to enable them.
     */
    public static boolean isGLEnabled(int pName) {
        if (!enableGLQueryStates) return false;

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
    //endregion
}
