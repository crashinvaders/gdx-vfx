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

package com.crashinvaders.vfx;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.framebuffer.VfxFrameBufferPool;
import com.crashinvaders.vfx.framebuffer.VfxFrameBufferRenderer;
import com.crashinvaders.vfx.utils.ViewportQuadMesh;

public class VfxRenderContext implements Disposable {

    private final VfxFrameBufferPool bufferPool;
    private final VfxFrameBufferRenderer bufferRenderer;
    private final Pixmap.Format pixelFormat;

    private int bufferWidth;
    private int bufferHeight;

    public VfxRenderContext(Pixmap.Format pixelFormat, int bufferWidth, int bufferHeight) {
        this.bufferPool = new VfxFrameBufferPool(pixelFormat, bufferWidth, bufferHeight, 8);
        this.bufferRenderer = new VfxFrameBufferRenderer();
        this.pixelFormat = pixelFormat;
        this.bufferWidth = bufferWidth;
        this.bufferHeight = bufferHeight;
    }

    @Override
    public void dispose() {
        bufferPool.dispose();
        bufferRenderer.dispose();
    }

    public void resize(int bufferWidth, int bufferHeight) {
        this.bufferWidth = bufferWidth;
        this.bufferHeight = bufferHeight;
        this.bufferPool.resize(bufferWidth, bufferHeight);
    }

    public VfxFrameBufferPool getBufferPool() {
        return bufferPool;
    }

    public void rebind() {
        bufferRenderer.rebind();
    }

    public Pixmap.Format getPixelFormat() {
        return pixelFormat;
    }

    public VfxFrameBufferRenderer getBufferRenderer() {
        return bufferRenderer;
    }

    public ViewportQuadMesh getViewportMesh() {
        return bufferRenderer.getMesh();
    }

    public int getBufferWidth() {
        return bufferWidth;
    }

    public int getBufferHeight() {
        return bufferHeight;
    }
}