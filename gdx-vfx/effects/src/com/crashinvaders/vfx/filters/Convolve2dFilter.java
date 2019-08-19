/*******************************************************************************
 * Copyright 2012 bmanuel
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

package com.crashinvaders.vfx.filters;

import com.crashinvaders.vfx.utils.ScreenQuadMesh;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;

/**
 * Encapsulates a separable 2D convolution kernel filter
 *
 * @author bmanuel
 * @author metaphore
 */
public final class Convolve2dFilter extends MultipassVfxFilter {

    private final int radius;
    private final int length; // NxN taps filter, w/ N=length

    private final float[] weights, offsetsHor, offsetsVert;

    private Convolve1dFilter hor, vert;

    public Convolve2dFilter(int radius) {
        this.radius = radius;
        length = (radius * 2) + 1;

        hor = new Convolve1dFilter(length);
        vert = new Convolve1dFilter(length, hor.weights);

        weights = hor.weights;
        offsetsHor = hor.offsets;
        offsetsVert = vert.offsets;
    }

    @Override
    public void dispose() {
        hor.dispose();
        vert.dispose();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void rebind() {
        hor.rebind();
        vert.rebind();
    }

    @Override
    public void render(ScreenQuadMesh mesh, PingPongBuffer buffer) {
        hor.setInput(buffer.getSrcTexture())
            .setOutput(buffer.getDstBuffer())
            .render(mesh);

        buffer.swap();

        vert.setInput(buffer.getSrcTexture())
            .setOutput(buffer.getDstBuffer())
            .render(mesh);
    }

    public int getRadius() {
        return radius;
    }

    public int getLength() {
        return length;
    }

    public float[] getWeights() {
        return weights;
    }

    public float[] getOffsetsHor() {
        return offsetsHor;
    }

    public float[] getOffsetsVert() {
        return offsetsVert;
    }
}
