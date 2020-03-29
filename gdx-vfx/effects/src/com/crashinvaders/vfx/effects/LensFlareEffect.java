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

/*******************************************************************************
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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.VfxPingPongWrapper;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/**
 * Lens flare effect.
 * @author Toni Sagrista
 **/
public class LensFlareEffect extends ShaderVfxEffect implements ChainVfxEffect {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_LIGHT_POSITION = "u_lightPosition";
    private static final String U_INTENSITY = "u_intensity";
    private static final String U_COLOR = "u_color";
    private static final String U_VIEWPORT = "u_viewport";

    private final Vector2 lightPosition = new Vector2(0.5f, 0.5f);
    private final Vector2 viewport = new Vector2();
    private final Vector3 color = new Vector3(1f, 0.8f, 0.2f);
    private float intensity = 5.0f;

    public LensFlareEffect() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                Gdx.files.classpath("gdxvfx/shaders/lens-flare.frag")));
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.set(width, height);
        setUniform(U_VIEWPORT, viewport);
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_LIGHT_POSITION, lightPosition);
        program.setUniformf(U_INTENSITY, intensity);
        program.setUniformf(U_COLOR, color);
        program.setUniformf(U_VIEWPORT, viewport);
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

    public Vector2 getLightPosition() {
        return lightPosition;
    }

    public void setLightPosition(Vector2 lightPosition) {
        setLightPosition(lightPosition.x, lightPosition.y);
    }

    /** Sets the light position in screen normalized coordinates [0..1].
     * @param x Light position x screen coordinate,
     * @param y Light position y screen coordinate. */
    public void setLightPosition(float x, float y) {
        lightPosition.set(x, y);
        setUniform(U_LIGHT_POSITION, lightPosition);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        setUniform(U_INTENSITY, intensity);
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Color color) {
        setColor(color.r, color.g, color.b);
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
        setUniform(U_COLOR, color);
    }
}
