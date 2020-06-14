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

public class CrtEffect extends ShaderVfxEffect implements ChainVfxEffect {
    private static final Vector2 tmpVec = new Vector2();

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_RESOLUTION = "u_resolution";

    private final Vector2 viewportSize = new Vector2();
    private SizeSource sizeSource = SizeSource.VIEWPORT;

    public CrtEffect() {
        this(LineStyle.HORIZONTAL_HARD, 1.3f, 0.5f);
    }

    /** Brightness is a value between [0..2] (default is 1.0). */
    public CrtEffect(LineStyle lineStyle, float brightnessMin, float brightnessMax) {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/crt.frag"),
                "#define SL_BRIGHTNESS_MIN " + brightnessMin + "\n" +
                "#define SL_BRIGHTNESS_MAX " + brightnessMax + "\n" +
                "#define LINE_TYPE " + lineStyle.ordinal()));
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        this.viewportSize.set(width, height);
        rebind();
    }

    @Override
    public void rebind () {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        switch (sizeSource) {
            case VIEWPORT:
                program.setUniformf(U_RESOLUTION, viewportSize);
                break;
            case SCREEN:
                program.setUniformf(U_RESOLUTION, tmpVec.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
                break;
        }
        program.end();
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

    public SizeSource getSizeSource() {
        return sizeSource;
    }

    /** Set shader resolution parameter source.
     * @see SizeSource */
    public void setSizeSource(SizeSource sizeSource) {
        if (sizeSource == null) {
            throw new IllegalArgumentException("Size source cannot be null.");
        }
        if (this.sizeSource == sizeSource) {
            return;
        }
        this.sizeSource = sizeSource;
        rebind();
    }

    /** Constant name/ordinal values match the respected #define constants from crt.frag */
    public enum LineStyle {
        CROSSLINE_HARD,
        VERTICAL_HARD,
        HORIZONTAL_HARD,
        VERTICAL_SMOOTH,
        HORIZONTAL_SMOOTH,
    }

    /** Shader resolution parameter source. */
    public enum SizeSource {
        /** Resolution will be resolved from the application internal viewport. */
        VIEWPORT,
        /** Resolution will be resolved from the application window size. */
        SCREEN,
    }
}