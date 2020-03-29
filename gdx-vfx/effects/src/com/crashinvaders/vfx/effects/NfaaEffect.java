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
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Normal filtered anti-aliasing filter.
 * @author Toni Sagrista
 * @author metaphore
 */
public class NfaaEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_VIEWPORT_INVERSE = "u_viewportInverse";

    private final Vector2 viewportInverse = new Vector2();

    public NfaaEffect(boolean supportAlpha) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/nfaa.frag"),
                supportAlpha ? "#define SUPPORT_ALPHA" : ""));
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_VIEWPORT_INVERSE, viewportInverse);
        program.end();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.viewportInverse.set(1f / width, 1f / height);
        setUniform(U_VIEWPORT_INVERSE, this.viewportInverse);
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
}
