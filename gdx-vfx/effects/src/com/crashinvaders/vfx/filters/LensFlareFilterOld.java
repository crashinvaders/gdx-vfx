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

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.crashinvaders.vfx.VfxFilterOld;
import com.crashinvaders.vfx.gl.VfxGLUtils;

/** Lens flare effect.
 * @author Toni Sagrista **/
public final class LensFlareFilterOld extends VfxFilterOld<LensFlareFilterOld> {

    private final Vector2 lightPosition = new Vector2(0.5f, 0.5f);
    private final Vector2 viewport = new Vector2();
    private final Vector3 color = new Vector3(1f, 0.8f, 0.2f);
    private float intensity = 5.0f;

    public enum Param implements Parameter {
        // @formatter:off
        Texture("u_texture0", 0),
        LightPosition("u_lightPosition", 2),
        Intensity("u_intensity", 0),
        Color("u_color", 3),
        Viewport("u_viewport", 2);
        // @formatter:on

        private String mnemonic;
        final int elementSize;

        private Param(String mnemonic, int arrayElementSize) {
            this.mnemonic = mnemonic;
            this.elementSize = arrayElementSize;
        }

        @Override
        public String mnemonic() {
            return this.mnemonic;
        }

        @Override
        public int arrayElementSize() {
            return this.elementSize;
        }
    }

    public LensFlareFilterOld() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/lensflare.frag")));
        rebind();
    }

    public Vector2 getLightPosition() {
        return lightPosition;
    }

    public void setLightPosition(Vector2 lightPosition) {
        setLightPosition(lightPosition.x, lightPosition.y);
    }

    /** Sets the light position in screen normalized coordinates [0..1]. */
    public void setLightPosition(float x, float y) {
        lightPosition.set(x, y);
        rebind();
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        rebind();
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
        rebind();
    }

    @Override
    public void resize(int width, int height) {
        viewport.set(width, height);
        rebind();
    }

    @Override
    public void rebind() {
        setParams(Param.Texture, u_texture0);
        setParams(Param.LightPosition, lightPosition);
        setParams(Param.Intensity, intensity);
        setParams(Param.Color, color);
        setParams(Param.Viewport, viewport);
        endParams();
    }

    @Override
    protected void onBeforeRender() {
        inputTexture.bind(u_texture0);
    }
}
