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

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;

/** Base class for any shader based single-pass filter. */
@SuppressWarnings("unchecked")
public abstract class ShaderVfxEffect extends AbstractVfxEffect {

    public static final int TEXTURE_HANDLE0 = 0;
    public static final int TEXTURE_HANDLE1 = 1;
    public static final int TEXTURE_HANDLE2 = 2;
    public static final int TEXTURE_HANDLE3 = 3;
    public static final int TEXTURE_HANDLE4 = 4;
    public static final int TEXTURE_HANDLE5 = 5;
    public static final int TEXTURE_HANDLE6 = 6;
    public static final int TEXTURE_HANDLE7 = 7;

    protected final ShaderProgram program;

    public ShaderVfxEffect(ShaderProgram program) {
        this.program = program;
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    @Override
    public void resize(int width, int height) {
        // Do nothing by default.
    }

    @Override
    public void rebind() {
        // Do nothing by default.
    }

    @Override
    public void update(float delta) {
        // Do nothing by default.
    }

    public ShaderProgram getProgram() {
        return program;
    }

    protected void renderShader(VfxRenderContext context, VfxFrameBuffer dst) {
        boolean manualBufferBind = !dst.isDrawing();
        if (manualBufferBind) { dst.begin(); }

        program.begin();
        context.getViewportMesh().render(program);
        program.end();

        if (manualBufferBind) { dst.end(); }
    }

    /**
     * Updates shader's uniform of float type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, float value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of int type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, int value) {
        program.begin();
        program.setUniformi(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of vec2 type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, Vector2 value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of vec3 type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, Vector3 value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of vec4 type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, Color value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of mat3 type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, Matrix3 value) {
        program.begin();
        program.setUniformMatrix(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform of mat4 type.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     */
    protected void setUniform(String uniformName, Matrix4 value) {
        program.begin();
        program.setUniformMatrix(uniformName, value);
        program.end();
    }

    /**
     * Updates shader's uniform array.
     * <p/>
     * <b>NOTE:</b> This is an utility method that will bind/unbind the shader program internally on every call.
     * If you need to update multiple uniforms, please consider calling methods directly from {@link ShaderProgram}.
     * @param elementSize Defines the type of the uniform array: float[], vec2[], vec3[] or vec4[].
     * Expected value is within the range of [1..4] (inclusively). */
    protected void setUniform(String uniformName, int elementSize, float[] values, int offset, int length) {
        program.begin();
        switch (elementSize) {
            case 1:
                program.setUniform1fv(uniformName, values, offset, length);
                break;
            case 2:
                program.setUniform2fv(uniformName, values, offset, length);
                break;
            case 3:
                program.setUniform3fv(uniformName, values, offset, length);
                break;
            case 4:
                program.setUniform4fv(uniformName, values, offset, length);
                break;
            default:
                throw new IllegalArgumentException("elementSize has illegal value: " + elementSize + ". Possible values are 1..4");
        }
        program.end();
    }
}
