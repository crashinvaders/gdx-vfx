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

public class GaussianBlurEffect extends AbstractVfxEffect implements ChainVfxEffect {

    private enum Tap {
        Tap3x3(1),
        Tap5x5(2),
        // Tap7x7(3),
        ;

        public final int radius;

        Tap(int radius) {
            this.radius = radius;
        }
    }

    public enum BlurType {
        Gaussian3x3(Tap.Tap3x3),
        Gaussian3x3b(Tap.Tap3x3), // R=5 (11x11, policy "higher-then-discard")
        Gaussian5x5(Tap.Tap5x5),
        Gaussian5x5b(Tap.Tap5x5), // R=9 (19x19, policy "higher-then-discard")
        ;

        public final Tap tap;

        BlurType(Tap tap) {
            this.tap = tap;
        }
    }

    private BlurType type;
    private float amount = 1f;
    private int passes = 1;

    private float invWidth, invHeight;
    private Convolve2DEffect convolve;

    public GaussianBlurEffect() {
        this(BlurType.Gaussian5x5);
    }

    public GaussianBlurEffect(BlurType blurType) {
        this.setType(blurType);
    }

    @Override
    public void dispose() {
        convolve.dispose();
    }

    @Override
    public void resize(int width, int height) {
        this.invWidth = 1f / (float) width;
        this.invHeight = 1f / (float) height;

        convolve.resize(width, height);
        computeBlurWeightings();
    }

    @Override
    public void rebind() {
        convolve.rebind();
        computeBlurWeightings();
    }

    @Override
    public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
        for (int i = 0; i < this.passes; i++) {
            convolve.render(context, buffers);

            if (i < this.passes - 1) {
                buffers.swap();
            }
        }
    }

    @Override
    public void update(float delta) {
        // Do nothing.
    }

    public BlurType getType() {
        return type;
    }

    public void setType(BlurType type) {
        if (type == null) {
            throw new IllegalArgumentException("Blur type cannot be null.");
        }
        if (this.type != type) {
            this.type = type;

            // Instantiate new matching convolve filter instance.
            if (convolve != null) {
                convolve.dispose();
            }
            convolve = new Convolve2DEffect(this.type.tap.radius);

            computeBlurWeightings();
        }
    }

    /** Warning: Not all blur types support custom amounts at this time */
    public float getAmount() {
        return amount;
    }

    /** Warning: Not all blur types support custom amounts at this time */
    public void setAmount(float amount) {
        this.amount = amount;
        computeBlurWeightings();
    }

    public int getPasses() {
        return passes;
    }

    public void setPasses(int passes) {
        if (passes < 1) throw new IllegalArgumentException("Passes should be greater than 0.");

        this.passes = passes;
    }

    private void computeBlurWeightings() {
        boolean hasData = true;

        float[] outWeights = convolve.getWeights();
        float[] outOffsetsH = convolve.getOffsetsHor();
        float[] outOffsetsV = convolve.getOffsetsVert();

        float dx = this.invWidth;
        float dy = this.invHeight;

        switch (this.type) {
            case Gaussian3x3:
            case Gaussian5x5:
                computeKernel(this.type.tap.radius, this.amount, outWeights);
                computeOffsets(this.type.tap.radius, this.invWidth, this.invHeight, outOffsetsH, outOffsetsV);
                break;

            case Gaussian3x3b:
                // Weights and offsets are computed from a binomial distribution
                // and reduced to be used *only* with bilinearly-filtered texture lookups
                // with radius = 1f

                // Weights
                outWeights[0] = 0.352941f;
                outWeights[1] = 0.294118f;
                outWeights[2] = 0.352941f;

                // Horizontal offsets
                outOffsetsH[0] = -1.33333f;
                outOffsetsH[1] = 0f;
                outOffsetsH[2] = 0f;
                outOffsetsH[3] = 0f;
                outOffsetsH[4] = 1.33333f;
                outOffsetsH[5] = 0f;

                // Vertical offsets
                outOffsetsV[0] = 0f;
                outOffsetsV[1] = -1.33333f;
                outOffsetsV[2] = 0f;
                outOffsetsV[3] = 0f;
                outOffsetsV[4] = 0f;
                outOffsetsV[5] = 1.33333f;

                // Scale offsets from binomial space to screen space
                for (int i = 0; i < convolve.getLength() * 2; i++) {
                    outOffsetsH[i] *= dx;
                    outOffsetsV[i] *= dy;
                }

                break;

            case Gaussian5x5b:

                // Weights and offsets are computed from a binomial distribution
                // and reduced to be used *only* with bilinearly-filtered texture lookups
                // with radius = 2f

                // weights
                outWeights[0] = 0.0702703f;
                outWeights[1] = 0.316216f;
                outWeights[2] = 0.227027f;
                outWeights[3] = 0.316216f;
                outWeights[4] = 0.0702703f;

                // Horizontal offsets
                outOffsetsH[0] = -3.23077f;
                outOffsetsH[1] = 0f;
                outOffsetsH[2] = -1.38462f;
                outOffsetsH[3] = 0f;
                outOffsetsH[4] = 0f;
                outOffsetsH[5] = 0f;
                outOffsetsH[6] = 1.38462f;
                outOffsetsH[7] = 0f;
                outOffsetsH[8] = 3.23077f;
                outOffsetsH[9] = 0f;

                // Vertical offsets
                outOffsetsV[0] = 0f;
                outOffsetsV[1] = -3.23077f;
                outOffsetsV[2] = 0f;
                outOffsetsV[3] = -1.38462f;
                outOffsetsV[4] = 0f;
                outOffsetsV[5] = 0f;
                outOffsetsV[6] = 0f;
                outOffsetsV[7] = 1.38462f;
                outOffsetsV[8] = 0f;
                outOffsetsV[9] = 3.23077f;

                // Scale offsets from binomial space to screen space
                for (int i = 0; i < convolve.getLength() * 2; i++) {
                    outOffsetsH[i] *= dx;
                    outOffsetsV[i] *= dy;
                }

                break;
            default:
                hasData = false;
                break;
        }

        if (hasData) {
            convolve.rebind();
        }
    }

    private void computeKernel(int blurRadius, float blurAmount, float[] outKernel) {
        int radius = blurRadius;

        // float sigma = (float)radius / amount;
        float sigma = blurAmount;

        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        float distance = 0.0f;
        int index = 0;

        for (int i = -radius; i <= radius; ++i) {
            distance = i * i;
            index = i + radius;
            outKernel[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += outKernel[index];
        }

        int size = (radius * 2) + 1;
        for (int i = 0; i < size; ++i) {
            outKernel[i] /= total;
        }
    }

    private void computeOffsets(int blurRadius, float dx, float dy, float[] outOffsetH, float[] outOffsetV) {
        int radius = blurRadius;

        final int X = 0, Y = 1;
        for (int i = -radius, j = 0; i <= radius; ++i, j += 2) {
            outOffsetH[j + X] = i * dx;
            outOffsetH[j + Y] = 0;

            outOffsetV[j + X] = 0;
            outOffsetV[j + Y] = i * dy;
        }
    }

    public static final class Convolve1DEffect extends ShaderVfxEffect implements ChainVfxEffect {

        private static final String U_TEXTURE = "u_texture0";
        private static final String U_SAMPLE_WEIGHTS = "u_sampleWeights";
        private static final String U_SAMPLE_OFFSETS = "u_sampleOffsets";

        public int length;
        public float[] weights;
        public float[] offsets;

        public Convolve1DEffect(int length) {
            this(length, new float[length], new float[length * 2]);
        }

        public Convolve1DEffect(int length, float[] weightsData) {
            this(length, weightsData, new float[length * 2]);
        }

        public Convolve1DEffect(int length, float[] weightsData, float[] offsets) {
            super(VfxGLUtils.compileShader(
                    Gdx.files.classpath("gdxvfx/shaders/screenspace.vert"),
                    Gdx.files.classpath("gdxvfx/shaders/convolve-1d.frag"),
                    "#define LENGTH " + length));
            setWeights(length, weightsData, offsets);
            rebind();
        }

        @Override
        public void rebind() {
            super.rebind();
            program.begin();
            program.setUniformi(U_TEXTURE, TEXTURE_HANDLE0);
            program.setUniform2fv(U_SAMPLE_OFFSETS, offsets, 0, length * 2); // LibGDX asks for number of floats, NOT number of elements.
            program.setUniform1fv(U_SAMPLE_WEIGHTS, weights, 0, length);
            program.end();
        }

        @Override
        public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
            render(context, buffers.getSrcBuffer(), buffers.getDstBuffer());
        }

        public void render(VfxRenderContext context, VfxFrameBuffer src, VfxFrameBuffer dst) {
            // Bind src buffer's texture as a primary one.
            src.getTexture().bind(TEXTURE_HANDLE0);
            // Apply shader effect.
            renderShader(context, dst);
        }

        public void setWeights(int length, float[] weights, float[] offsets) {
            this.weights = weights;
            this.length = length;
            this.offsets = offsets;
        }
    }

    /** Encapsulates a separable 2D convolution kernel filter. */
    public static final class Convolve2DEffect extends CompositeVfxEffect implements ChainVfxEffect {

        private final int radius;
        private final int length; // NxN taps filter, w/ N=length
        private final float[] weights, offsetsHor, offsetsVert;

        private Convolve1DEffect hor, vert;

        public Convolve2DEffect(int radius) {
            this.radius = radius;
            length = (radius * 2) + 1;

            hor = register(new Convolve1DEffect(length));
            vert = register(new Convolve1DEffect(length, hor.weights));

            weights = hor.weights;
            offsetsHor = hor.offsets;
            offsetsVert = vert.offsets;
        }

        @Override
        public void render(VfxRenderContext context, VfxPingPongWrapper buffers) {
            hor.render(context, buffers);
            buffers.swap();
            vert.render(context, buffers);
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
}
