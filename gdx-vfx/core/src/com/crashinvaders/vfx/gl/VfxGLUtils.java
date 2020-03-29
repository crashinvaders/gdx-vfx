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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class VfxGLUtils {
    private static final String TAG = VfxGLUtils.class.getSimpleName();
    private static final IntBuffer tmpIntBuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();
    private static final ByteBuffer tmpByteBuffer = BufferUtils.newByteBuffer(32);
    private static final VfxGlViewport tmpViewport = new VfxGlViewport();

    /** The code that is always added to the vertex shader code.
     * Note that this is added as-is, you should include a newline (`\n`) if needed. */
    public static String prependVertexCode = "";

    /** The code that is always added to every fragment shader code.
     * Note that this is added as-is, you should include a newline (`\n`) if needed. */
    public static String prependFragmentCode = "";

    //TODO Remove this after https://github.com/libgdx/libgdx/issues/4688 gets resolved
    /** This field is used to provide custom GL calls implementation. */
    public static VfxGlExtension glExtension;
    static {
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            try {
                glExtension = (VfxGlExtension) ClassReflection.newInstance(
                        ClassReflection.forName("com.crashinvaders.vfx.gwt.GwtVfxGlExtension"));
                Gdx.app.log(TAG, "GWT GL Extension initialized.");
            } catch (ReflectionException e) {
                throw new GdxRuntimeException("Cannot find GwtVfxGlExtension class." +
                        "Are you sure you connected \"gdx-vfx-gwt\" library? " +
                        "\n" +
                        "Please visit GWT setup wiki page for instructions: " +
                        "https://github.com/crashinvaders/gdx-vfx/wiki/GWT-HTML-Library-Integration", e);
            }
        } else {
            glExtension = new DefaultVfxGlExtension();
        }
    }

    public static int getBoundFboHandle() {
        return glExtension.getBoundFboHandle();
    }

    public static VfxGlViewport getViewport() {
        IntBuffer intBuf = tmpIntBuf;
        Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf);
        return tmpViewport.set(intBuf.get(0), intBuf.get(1), intBuf.get(2), intBuf.get(3));
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

        String prependVert = prependVertexCode + defines;
        String prependFrag = prependFragmentCode + defines;
        String srcVert = vertexFile.readString();
        String srcFrag = fragmentFile.readString();

        ShaderProgram shader = new ShaderProgram(prependVert + "\n" + srcVert, prependFrag + "\n" + srcFrag);

        if (!shader.isCompiled()) {
            throw new GdxRuntimeException("Shader compile error: " + vertexFile.name() + "/" + fragmentFile.name() + "\n" + shader.getLog());
        }
        return shader;
    }

    //region GL state queries

    /** Enable pipeline state queries: beware the pipeline can stall! */
    public static boolean enableGLQueryStates = false;

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
                Gdx.gl20.glGetBooleanv(GL20.GL_BLEND, tmpByteBuffer);
                result = (tmpByteBuffer.get() == 1);
                tmpByteBuffer.clear();
                break;
            default:
                result = false;
        }

        return result;
    }
    //endregion
}
