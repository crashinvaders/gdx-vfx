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
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public class FilmGrainEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_SEED = "u_seed";
    private static final String U_NOISE_AMOUNT = "u_noiseAmount";

    private float seed = 0f;
    private float noiseAmount = 0.18f;

    public FilmGrainEffect() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/film-grain.frag")));
        rebind();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float newSeedValue = (this.seed + delta) % 1f;
        setSeed(newSeedValue);
    }

    @Override
    public void rebind () {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_SEED, seed);
        program.setUniformf(U_NOISE_AMOUNT, noiseAmount);
        program.begin();
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        render(context, buffers.getSrcBuffer(), buffers.getDstBuffer());
    }

    public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
        // Bind src buffer's texture as a primary one.
        src.getTexture().bind(TEXTURE_HANDLE0);
        // Apply shader effect and render result to dst buffer.
        renderShader(context, dst);
    }

    public float getSeed() {
        return seed;
    }

    public void setSeed(float seed) {
        this.seed = seed;
        setUniform(U_SEED, seed);
    }

    public float getNoiseAmount() {
        return noiseAmount;
    }

    public void setNoiseAmount(float noiseAmount) {
        this.noiseAmount = noiseAmount;
        setUniform(U_NOISE_AMOUNT, noiseAmount);
    }
}
