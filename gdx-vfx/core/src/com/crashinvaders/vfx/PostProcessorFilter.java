/*******************************************************************************
 * Copyright 2012 bmanuel
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
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.common.framebuffer.FboWrapper;
import com.crashinvaders.vfx.utils.FullscreenQuad;

/** The base class for any single-pass filter. */
@SuppressWarnings("unchecked")
public abstract class PostProcessorFilter<T extends PostProcessorFilter> implements Disposable {

    public interface Parameter {
        String mnemonic();
        int arrayElementSize();
    }

    //TODO Make not static or move out of this class.
    protected static final FullscreenQuad quad = new FullscreenQuad();

    protected static final int u_texture0 = 0;
    protected static final int u_texture1 = 1;
    protected static final int u_texture2 = 2;
    protected static final int u_texture3 = 3;

    protected final ShaderProgram program;

    protected Texture inputTexture = null;
    protected FboWrapper outputBuffer = null;

    private boolean programBegan = false;

    public PostProcessorFilter(ShaderProgram program) {
        this.program = program;
    }

    public T setInput(Texture input) {
        this.inputTexture = input;
        return (T)this; // Assumes T extends PostProcessorFilter
    }

    public T setInput(FboWrapper input) {
        return setInput(input.getFbo().getColorBufferTexture());
    }

    public T setOutput(FboWrapper output) {
        this.outputBuffer = output;
        return (T)this; // Assumes T extends PostProcessorFilter
    }

    @Override
    public void dispose() {
        program.dispose();
    }

    /**
     * This method should be called once filter will be added to {@link PostProcessorEffect}.
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
	 * and unbound once per call: for a batch-ready version of this fuction see and use setParams instead.
	 */

    public final void render() {
        boolean manualBufferBind = outputBuffer != null && !outputBuffer.isDrawing();
        if (manualBufferBind) { outputBuffer.begin(); }

        // Gives a chance to filters to perform needed operations just before the rendering operation takes place.
        onBeforeRender();

        program.begin();
        quad.render(program);
        program.end();

        if (manualBufferBind) { outputBuffer.end(); }
    }

    /** This method gets called just before rendering. */
    protected abstract void onBeforeRender();

    /** int */
    protected void setParam(Parameter param, int value) {
        program.begin();
        program.setUniformi(param.mnemonic(), value);
        program.end();
    }

    /** float */
    protected void setParam(Parameter param, float value) {
        program.begin();
        program.setUniformf(param.mnemonic(), value);
        program.end();
    }

    /** vec2 */
    protected void setParam(Parameter param, Vector2 value) {
        program.begin();
        program.setUniformf(param.mnemonic(), value);
        program.end();
    }

    /** vec3 */
    protected void setParam(Parameter param, Vector3 value) {
        program.begin();
        program.setUniformf(param.mnemonic(), value);
        program.end();
    }

    /** mat3 */
    protected T setParam(Parameter param, Matrix3 value) {
        program.begin();
        program.setUniformMatrix(param.mnemonic(), value);
        program.end();
        return (T) this;
    }

    /** mat4 */
    protected T setParam(Parameter param, Matrix4 value) {
        program.begin();
        program.setUniformMatrix(param.mnemonic(), value);
        program.end();
        return (T) this;
    }

    /** float[], vec2[], vec3[], vec4[] */
    protected T setParamv(Parameter param, float[] values, int offset, int length) {
        program.begin();

        switch (param.arrayElementSize()) {
            case 4:
                program.setUniform4fv(param.mnemonic(), values, offset, length);
                break;
            case 3:
                program.setUniform3fv(param.mnemonic(), values, offset, length);
                break;
            case 2:
                program.setUniform2fv(param.mnemonic(), values, offset, length);
                break;
            default:
            case 1:
                program.setUniform1fv(param.mnemonic(), values, offset, length);
                break;
        }

        program.end();
        return (T) this;
    }

    //region
    // Utility methods to set the parameter to the specified value for the filter.
    // When you are finished building the batch you shall signal it by invoking endParams().
    //TODO Rename/move the methods under a subclass to avoid naming ambiguity between setParam/setParams.

    /** float */
    protected T setParams(Parameter param, float value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformf(param.mnemonic(), value);
        return (T) this;
    }

    /** int */
    protected T setParams(Parameter param, int value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformi(param.mnemonic(), value);
        return (T) this;
    }

    /** vec2 */
    protected T setParams(Parameter param, Vector2 value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformf(param.mnemonic(), value);
        return (T) this;
    }

    /** vec3 */
    protected T setParams(Parameter param, Vector3 value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformf(param.mnemonic(), value);
        return (T) this;
    }

    /** mat3 */
    protected T setParams(Parameter param, Matrix3 value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformMatrix(param.mnemonic(), value);
        return (T) this;
    }

    /** mat4 */
    protected T setParams(Parameter param, Matrix4 value) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }
        program.setUniformMatrix(param.mnemonic(), value);
        return (T) this;
    }

    /** float[], vec2[], vec3[], vec4[] */
    protected T setParamsv(Parameter param, float[] values, int offset, int length) {
        if (!programBegan) {
            programBegan = true;
            program.begin();
        }

        switch (param.arrayElementSize()) {
            case 4:
                program.setUniform4fv(param.mnemonic(), values, offset, length);
                break;
            case 3:
                program.setUniform3fv(param.mnemonic(), values, offset, length);
                break;
            case 2:
                program.setUniform2fv(param.mnemonic(), values, offset, length);
                break;
            default:
            case 1:
                program.setUniform1fv(param.mnemonic(), values, offset, length);
                break;
        }
        return (T) this;
    }

    /** Should be called after any one or more setParams method calls. */
    protected void endParams() {
        if (programBegan) {
            program.end();
            programBegan = false;
        }
    }
    //endregion
}
