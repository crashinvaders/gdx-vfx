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

import com.badlogic.gdx.Gdx;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class NoiseEffect extends ShaderVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_AMOUNT = "u_amount";
    private static final String U_SPEED = "u_speed";
    private static final String U_TIME = "u_time";

    private float amount;
    private float speed;
    private float time = 0f;

    public NoiseEffect(float amount, float speed) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/noise.frag")));
        this.amount = amount;
        this.speed = speed;
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_AMOUNT, amount);
        program.setUniformf(U_SPEED, speed);
        program.setUniformf(U_TIME, time);
        program.end();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setTime(this.time + delta);
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
        setUniform(U_TIME, time);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
        setUniform(U_AMOUNT, amount);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        setUniform(U_SPEED, speed);
    }
}

