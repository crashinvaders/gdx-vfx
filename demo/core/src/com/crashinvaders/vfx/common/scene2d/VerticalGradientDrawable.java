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

package com.crashinvaders.vfx.common.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class VerticalGradientDrawable extends BaseDrawable {
    private static final int VERT_SIZE = 5;
    private static final int COMP_X = 0;
    private static final int COMP_Y = 1;
    private static final int COMP_COLOR = 2;
    private static final int COMP_U = 3;
    private static final int COMP_V = 4;

    private final float[] vertices = new float[4 * VERT_SIZE];
    private final TextureRegion region;

    public VerticalGradientDrawable(Texture texture) {
        this(new TextureRegion(texture));
    }

    public VerticalGradientDrawable(TextureRegionDrawable drawable) {
        this(drawable.getRegion());
    }

    public VerticalGradientDrawable(TextureRegion region) {
        this.region = region;

        // Assign region UV values to the vertices.
        vertices[0*VERT_SIZE + COMP_U] = region.getU();
        vertices[0*VERT_SIZE + COMP_V] = region.getV();
        vertices[1*VERT_SIZE + COMP_U] = region.getU();
        vertices[1*VERT_SIZE + COMP_V] = region.getV2();
        vertices[2*VERT_SIZE + COMP_U] = region.getU2();
        vertices[2*VERT_SIZE + COMP_V] = region.getV2();
        vertices[3*VERT_SIZE + COMP_U] = region.getU2();
        vertices[3*VERT_SIZE + COMP_V] = region.getV();
    }

    public VerticalGradientDrawable setColors(Color colorBottom, Color colorTop) {
        float colorBitsBottom = colorBottom.toFloatBits();
        float colorBitsTop = colorTop.toFloatBits();

        // Update vertices' colors.
        vertices[0*VERT_SIZE + COMP_COLOR] = colorBitsBottom;
        vertices[1*VERT_SIZE + COMP_COLOR] = colorBitsTop;
        vertices[2*VERT_SIZE + COMP_COLOR] = colorBitsTop;
        vertices[3*VERT_SIZE + COMP_COLOR] = colorBitsBottom;

        return this;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        // Update vertices' position.
        final float fx2 = x + width;
        final float fy2 = y + height;
        vertices[0*VERT_SIZE + COMP_X] = x;
        vertices[0*VERT_SIZE + COMP_Y] = y;
        vertices[1*VERT_SIZE + COMP_X] = x;
        vertices[1*VERT_SIZE + COMP_Y] = fy2;
        vertices[2*VERT_SIZE + COMP_X] = fx2;
        vertices[2*VERT_SIZE + COMP_Y] = fy2;
        vertices[3*VERT_SIZE + COMP_X] = fx2;
        vertices[3*VERT_SIZE + COMP_Y] = y;

        batch.draw(region.getTexture(), vertices, 0, vertices.length);
    }
}
