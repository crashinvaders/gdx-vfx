/*******************************************************************************
 * Copyright 2019 metaphore
 <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 <p>
 *   http://www.apache.org/licenses/LICENSE-2.0
 <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Implements the Dual Kawase blur algorithm using an efficient downsampling
 * and upsampling frame buffer pyramid.
 * @author groxar
 */
public class DualKawaseBlurEffect extends CompositeVfxEffect implements ChainVfxEffect {

    private final DownsampleEffect downsampleEffect;
    private final UpsampleEffect upsampleEffect;

    private int passes;
    private float offset;

    private int width;
    private int height;

    private VfxFrameBuffer[] pyramidBuffers;

    public DualKawaseBlurEffect() {
        this(4, 1.0f);
    }

    public DualKawaseBlurEffect(int passes, float offset) {
        if (passes < 1) throw new IllegalArgumentException("Passes must be >= 1");

        this.passes = passes;
        this.offset = offset;

        this.downsampleEffect = register(new DownsampleEffect());
        this.upsampleEffect = register(new UpsampleEffect());

        setOffset(offset);
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        super.resize(width, height);
        rebuildPyramid();
    }

    @Override
    public void dispose() {
        super.dispose();
        disposePyramid();
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        if (passes < 1) throw new IllegalArgumentException("Passes must be >= 1");
        if (this.passes != passes) {
            this.passes = passes;
            rebuildPyramid();
        }
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
        downsampleEffect.setOffset(offset);
        upsampleEffect.setOffset(offset);
    }

    private void disposePyramid() {
        if (pyramidBuffers != null) {
            for (int i = 0; i < pyramidBuffers.length; i++) {
                if (pyramidBuffers[i] != null) {
                    pyramidBuffers[i].dispose();
                }
            }
            pyramidBuffers = null;
        }
    }

    private void rebuildPyramid() {
        disposePyramid();

        if (width <= 0 || height <= 0 || passes <= 0) return;

        pyramidBuffers = new VfxFrameBuffer[passes];
        int w = width;
        int h = height;

        for (int i = 0; i < passes; i++) {
            w = Math.max(1, (w + 1) / 2);
            h = Math.max(1, (h + 1) / 2);

            VfxFrameBuffer buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);
            buffer.initialize(w, h);

            Texture texture = buffer.getTexture();
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            texture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

            pyramidBuffers[i] = buffer;
        }
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Downsample pass
        // Step 0: from buffers.getSrcBuffer() to pyramidBuffers[0]
        VfxFrameBuffer src = buffers.getSrcBuffer();
        src.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        downsampleEffect.render(context, src, pyramidBuffers[0]);

        for (int i = 1; i < passes; i++) {
            downsampleEffect.render(context, pyramidBuffers[i - 1], pyramidBuffers[i]);
        }

        // Upsample pass
        // Step passes-1 down to 1: from pyramidBuffers[i] to pyramidBuffers[i-1]
        for (int i = passes - 1; i > 0; i--) {
            upsampleEffect.render(context, pyramidBuffers[i], pyramidBuffers[i - 1]);
        }

        // Final step: from pyramidBuffers[0] to buffers.getDstBuffer()
        upsampleEffect.render(context, pyramidBuffers[0], buffers.getDstBuffer());
    }

    private static final class DownsampleEffect extends ShaderVfxEffect {
        private static final String U_TEXTURE0 = "u_texture0";
        private static final String U_TEXEL = "u_texel";
        private static final String U_OFFSET = "u_offset";

        private final Vector2 texelSize = new Vector2();
        private float offset = 1.0f;

        public DownsampleEffect() {
            super(VfxGLUtils.compileShader(
                    Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                    Gdx.files.classpath("gdxvfx/shaders/dual-kawase-down.frag")));
            rebind();
        }

        @Override
        public void rebind() {
            super.rebind();
            program.bind();
            program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
            program.setUniformf(U_OFFSET, offset);
        }

        public void setOffset(float offset) {
            this.offset = offset;
            setUniform(U_OFFSET, offset);
        }

        public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
            int srcWidth = src.getFbo().getWidth();
            int srcHeight = src.getFbo().getHeight();

            texelSize.set(1f / srcWidth, 1f / srcHeight);

            src.getTexture().bind(TEXTURE_HANDLE0);

            program.bind();
            program.setUniformf(U_TEXEL, texelSize);

            renderShader(context, dst);
        }
    }

    private static final class UpsampleEffect extends ShaderVfxEffect {
        private static final String U_TEXTURE0 = "u_texture0";
        private static final String U_TEXEL = "u_texel";
        private static final String U_OFFSET = "u_offset";

        private final Vector2 texelSize = new Vector2();
        private float offset = 1f;

        public UpsampleEffect() {
            super(VfxGLUtils.compileShader(
                    Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                    Gdx.files.classpath("gdxvfx/shaders/dual-kawase-up.frag")));
            rebind();
        }

        @Override
        public void rebind() {
            super.rebind();
            program.bind();
            program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
            program.setUniformf(U_OFFSET, offset);
        }

        public void setOffset(float offset) {
            this.offset = offset;
            setUniform(U_OFFSET, offset);
        }

        public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
            int srcWidth = src.getFbo().getWidth();
            int srcHeight = src.getFbo().getHeight();

            texelSize.set(1f / srcWidth, 1f / srcHeight);

            src.getTexture().bind(TEXTURE_HANDLE0);

            program.bind();
            program.setUniformf(U_TEXEL, texelSize);

            renderShader(context, dst);
        }
    }
}