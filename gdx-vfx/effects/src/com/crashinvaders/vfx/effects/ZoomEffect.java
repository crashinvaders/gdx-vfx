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
import com.badlogic.gdx.utils.Align;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Simple zooming effect. */
public class ZoomEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_OFFSET_X = "u_offsetX";
    private static final String U_OFFSET_Y = "u_offsetY";
    private static final String U_ZOOM = "u_zoom";

    private float originX = 0.5f;
    private float originY = 0.5f;
    private float zoom = 1f;

    public ZoomEffect() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/zoom.vert"),
                Gdx.files.classpath("gdxvfx/shaders/zoom.frag")));
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, 0);
        program.setUniformf(U_OFFSET_X, originX);
        program.setUniformf(U_OFFSET_Y, originY);
        program.setUniformf(U_ZOOM, zoom);
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

    public float getOriginX() {
        return originX;
    }

    public float getOriginY() {
        return originY;
    }

    /**
     * Specify the zoom origin in {@link Align} bits.
     * @see Align
     */
    public void setOrigin(int align) {
        final float originX;
        final float originY;
        if ((align & Align.left) != 0) {
            originX = 0f;
        } else if ((align & Align.right) != 0) {
            originX = 1f;
        } else {
            originX = 0.5f;
        }
        if ((align & Align.bottom) != 0) {
            originY = 0f;
        } else if ((align & Align.top) != 0) {
            originY = 1f;
        } else {
            originY = 0.5f;
        }
        setOrigin(originX, originY);
    }

    /**
     * Specify the zoom origin in normalized screen coordinates.
     * @param originX horizontal origin [0..1].
     * @param originY vertical origin [0..1].
     */
    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;

        program.begin();
        program.setUniformf(U_OFFSET_X, originX);
        program.setUniformf(U_OFFSET_Y, originY);
        program.end();
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
        setUniform(U_ZOOM, zoom);
    }
}
