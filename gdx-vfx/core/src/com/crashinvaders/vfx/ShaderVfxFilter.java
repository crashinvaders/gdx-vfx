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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;

/**
 * Base class for any single-pass filter.
 */
@SuppressWarnings("unchecked")
public abstract class ShaderVfxFilter<T extends ShaderVfxFilter> implements VfxFilterNew {

    static protected final int TEXTURE0 = 0;
    static protected final int TEXTURE1 = 1;
    static protected final int TEXTURE2 = 2;
    static protected final int TEXTURE3 = 3;

    protected final ShaderProgram program;

    protected Texture inputTexture = null;
    protected VfxFrameBuffer outputBuffer = null;

    private boolean programBegan = false;

    public ShaderVfxFilter(ShaderProgram program) {
        this.program = program;
    }

    public T setInput(Texture input) {
        this.inputTexture = input;
        return (T)this; // Assumes T extends VfxFilter
    }

    public T setInput(VfxFrameBuffer input) {
        return setInput(input.getFbo().getColorBufferTexture());
    }

    public T setOutput(VfxFrameBuffer output) {
        this.outputBuffer = output;
        return (T)this; // Assumes T extends VfxFilter
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    /**
     * This method should be called once filter will be added to {@link VfxEffectOld}.
     * Also it must be called on every application resize as usual.
     */
    public abstract void resize(int width, int height);

    /**
     * Concrete objects shall be responsible to recreate or rebind its own resources whenever its needed, usually when the OpenGL
     * context is lost. Eg., framebuffer textures should be updated and shader parameters should be reuploaded/rebound.
     */
    public abstract void rebind();

	/*
     * Sets the parameter to the specified value for this filter. This is for one-off operations since the shader is being bound
	 * and unbound once per call: for a batch-ready version of this function see and use setParams instead.
	 */
    public void render(ViewportQuadMesh mesh) {
        boolean manualBufferBind = outputBuffer != null && !outputBuffer.isDrawing();
        if (manualBufferBind) { outputBuffer.begin(); }

        // Gives a chance to filters to perform needed operations just before the rendering operation takes place.
        onBeforeRender();

        program.begin();
        mesh.render(program);
        program.end();

        if (manualBufferBind) { outputBuffer.end(); }
    }

    /** This method gets called just before rendering. */
    protected abstract void onBeforeRender();

    /** Updates shader's uniform of float type. */
    public void setUniform(String uniformName, float value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform of int type. */
    public void setUniform(String uniformName, int value) {
        program.begin();
        program.setUniformi(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform of vec2 type. */
    public void setUniform(String uniformName, Vector2 value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform of vec3 type. */
    public void setUniform(String uniformName, Vector3 value) {
        program.begin();
        program.setUniformf(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform of mat3 type. */
    public void setUniform(String uniformName, Matrix3 value) {
        program.begin();
        program.setUniformMatrix(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform of mat4 type. */
    public void setUniform(String uniformName, Matrix4 value) {
        program.begin();
        program.setUniformMatrix(uniformName, value);
        program.end();
    }

    /** Updates shader's uniform array.
     * @param elementSize could be 1..4 and defines type of the uniform array: float[], vec2[], vec3[] or vec4[]. */
    public void setUniform(String uniformName, int elementSize, float[] values, int offset, int length) {
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
