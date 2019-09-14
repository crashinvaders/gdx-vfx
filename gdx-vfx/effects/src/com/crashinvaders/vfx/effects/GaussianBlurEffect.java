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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.crashinvaders.vfx.VfxEffectOld;
import com.crashinvaders.vfx.framebuffer.RegularPingPongBuffer;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;
import com.crashinvaders.vfx.filters.CopyFilterOld;
import com.crashinvaders.vfx.filters.GaussianBlurFilter;

public class GaussianBlurEffect extends VfxEffectOld {

    private final PingPongBuffer pingPongBuffer;
    private final CopyFilterOld copy;
    private final GaussianBlurFilter blur;

    private boolean blending = false;
    private int sfactor, dfactor;

    // To keep track of the first render call.
    private boolean firstRender = true;

    public GaussianBlurEffect() {
        this(8, GaussianBlurFilter.BlurType.Gaussian5x5);
    }

    public GaussianBlurEffect(int blurPasses, GaussianBlurFilter.BlurType blurType) {
        pingPongBuffer = new RegularPingPongBuffer(Pixmap.Format.RGBA8888);

        copy = new CopyFilterOld();

        blur = new GaussianBlurFilter();
        blur.setPasses(blurPasses);
        blur.setType(blurType);
    }

    @Override
    public void dispose() {
        pingPongBuffer.dispose();
        blur.dispose();
        copy.dispose();
    }

    @Override
    public void resize(int width, int height) {
        pingPongBuffer.resize(width, height);
        blur.resize(width, height);
        copy.resize(width, height);
    }

    @Override
    public void rebind() {
        pingPongBuffer.rebind();
        blur.rebind();
        copy.rebind();
    }

    @Override
    public void render(ViewportQuadMesh mesh, VfxFrameBuffer src, VfxFrameBuffer dst) {
        if (blur.getPasses() < 1) {
            // Do not apply blur filter.
            copy.setInput(src).setOutput(dst).render(mesh);
            return;
        }

        boolean blendingWasEnabled = VfxGLUtils.isGLEnabled(GL20.GL_BLEND);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        pingPongBuffer.begin();
        copy.setInput(src).setOutput(pingPongBuffer.getDstBuffer()).render(mesh);
        pingPongBuffer.swap();
        // Blur filter performs multiple passes of mixing ping-pong buffers and expects src and dst to have valid data.
        // So for the first run we just make both src and dst buffers identical.
        if (firstRender) {
            firstRender = false;
            copy.setInput(src).setOutput(pingPongBuffer.getDstBuffer()).render(mesh);
            pingPongBuffer.swap();
        }
        blur.render(mesh, pingPongBuffer);
        pingPongBuffer.end();

        if (blending || blendingWasEnabled) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
        }

        if (blending) {
            // TODO support for Gdx.gl.glBlendFuncSeparate(sfactor, dfactor, GL20.GL_ONE, GL20.GL_ONE );
            Gdx.gl.glBlendFunc(sfactor, dfactor);
        }

        copy.setInput(pingPongBuffer.getDstTexture())
                .setOutput(dst)
                .render(mesh);
    }

    public GaussianBlurEffect enableBlending(int sfactor, int dfactor) {
        this.blending = true;
        this.sfactor = sfactor;
        this.dfactor = dfactor;
        return this;
    }

    public void disableBlending() {
        this.blending = false;
    }

    public GaussianBlurEffect setBlurPasses(int blurPasses) {
        blur.setPasses(blurPasses);
        return this;
    }

    public int getBlurPasses() {
        return blur.getPasses();
    }
}
