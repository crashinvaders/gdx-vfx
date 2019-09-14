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

package com.crashinvaders.vfx.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;

public class UniformBatcher implements Pool.Poolable {

    private ShaderProgram program = null;
    private boolean activateShader = false;

    @Override
    public void reset() {
        program = null;
        activateShader = false;
    }

    public UniformBatcher begin(ShaderProgram program, boolean activateShader) {
        this.program = program;
        this.activateShader = activateShader;

        if (activateShader) {
            program.begin();
        }

        return this;
    }

    /** Should be called after set* method calls. */
    public void end() {
        if (activateShader) {
            program.end();
        }
    }

    /** Updates shader's uniform of float type. */
    public UniformBatcher set(String uniformName, float value) {
        program.setUniformf(uniformName, value);
        return this;
    }

    /** Updates shader's uniform of int type. */
    public UniformBatcher set(String uniformName, int value) {
        program.setUniformi(uniformName, value);
        return this;
    }

    /** Updates shader's uniform of vec2 type. */
    public UniformBatcher set(String uniformName, Vector2 value) {
        program.setUniformf(uniformName, value);
        return this;
    }

    /** Updates shader's uniform of vec3 type. */
    public UniformBatcher set(String uniformName, Vector3 value) {
        program.setUniformf(uniformName, value);
        return this;
    }

    /** Updates shader's uniform of mat3 type. */
    public UniformBatcher set(String uniformName, Matrix3 value) {
        program.setUniformMatrix(uniformName, value);
        return this;
    }

    /** Updates shader's uniform of mat4 type. */
    public UniformBatcher set(String uniformName, Matrix4 value) {
        program.setUniformMatrix(uniformName, value);
        return this;
    }

    /** Updates shader's uniform array.
     * @param elementSize could be 1..4 and defines type of the uniform array: float[], vec2[], vec3[] or vec4[]. */
    public UniformBatcher set(String uniformName, int elementSize, float[] values, int offset, int length) {
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
        return this;
    }
}