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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/** @author Xoppa
 * https://github.com/xoppa/world/blob/master/src/com/xoppa/android/misc/RadialSprite.java */
public class RadialDrawable implements Drawable {
    private final static int TOPRIGHT1 = 0;
    private final static int BOTTOMRIGHT1 = 5;
    private final static int BOTTOMLEFT1 = 10;
    private final static int TOPLEFT1 = 15;
    private final static int TOPRIGHT2 = 20;
    private final static int BOTTOMRIGHT2 = 25;
    private final static int BOTTOMLEFT2 = 30;
    private final static int TOPLEFT2 = 35;
    private final static int TOPRIGHT3 = 40;
    private final static int BOTTOMRIGHT3 = 45;
    private final static int BOTTOMLEFT3 = 50;
    private final static int TOPLEFT3 = 55;

    private final Vector2 tmp = new Vector2();

    private Texture texture;
    private final float[] verts = new float[60];
    private float x, y, angle, width, height, u1, u2, v1, v2, du, dv;
    private boolean dirty = true;
    private int draw = 0;
    private float angleOffset = 270f;
    private float originX, originY;
    private float scaleX = 1f, scaleY = 1f;
    private StartAngle startAngle = StartAngle.DOWN;
    private boolean clockwiseDirection;
    //params for calculation optimisation
    private float xc, yc, x2;

    public enum StartAngle {
        TOP(180f),
        RIGHT(90f),
        DOWN(0f),
        LEFT(90f);

        public final float shiftAngle;

        StartAngle(float shiftAngle) {
            this.shiftAngle = shiftAngle;
        }
    }

    public RadialDrawable(final TextureRegion textureRegion) {
        setTextureRegion(textureRegion);
    }

    public void setStartAngle(StartAngle startAngle) {
        this.startAngle = startAngle;
    }

    public void setClockwiseDirection(boolean clockwiseDirection) {
        this.clockwiseDirection = clockwiseDirection;
    }

    private void setColor(float packedColor) {
        for (int i = 0; i < 12; i++)
            verts[i*5+2] = packedColor;
    }

    private void vert(final float[] verts, final int offset, float x, float y) {
        if (clockwiseDirection) {
            //Inverse x for clockwise direction
            x = x2 - x + this.x;
        }
        //this approach works only for texture regions with equal width and height(square area)
        tmp.set(x - xc, y - yc).rotate(startAngle.shiftAngle).add(xc, yc);
        x = tmp.x;
        y = tmp.y;
        final float u = u1 + du * ((x - this.x) / this.width);
        final float v = v1 + dv * (1f - ((y - this.y) / this.height));
        vert(verts, offset, x, y, u, v);
    }

    private void vert(final float[] verts, final int offset, final float x, final float y, final float u, final float v) {
        verts[offset] = this.x + originX + (x - this.x - originX) * scaleX;
        verts[offset+1] = this.y + originY + (y - this.y - originY) * scaleY;
        verts[offset+3] = u;
        verts[offset+4] = v;
    }

    protected void calculate(float x, float y, float width, float height, float angle, float u0, float v0, float u1, float v1) {
        if (!this.dirty && this.x == x && this.y == y && this.angle == angle && this.width == width && this.height == height
                && this.u1 == u0 && this.v2 == v1 && this.u2 == u1 && this.v2 == v1)
            return;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.angle = angle;
        this.u1 = u0;
        this.v1 = v0;
        this.u2 = u1;
        this.v2 = v1;
        final float centerX = width * 0.5f;
        final float centerY = height * 0.5f;
        final float x2 = x + width;
        final float y2 = y + height;
        final float xc = x + centerX;
        final float yc = y + centerY;
        this.xc = xc;
        this.yc = yc;
        this.x2 = x2;
        final float ax = MathUtils.cosDeg(angle+angleOffset); // positive right, negative left
        final float ay = MathUtils.sinDeg(angle+angleOffset); // positive top, negative bottom
        final float txa = ax != 0f ? Math.abs(centerX / ax) : 99999999f; // intersection on left or right "wall"
        final float tya = ay != 0f ? Math.abs(centerY / ay) : 99999999f; // intersection on top or bottom "wall"
        final float t = Math.min(txa, tya);
        // tx and ty are the intersection points relative to centerX and centerY.
        final float tx = t*ax;
        final float ty = t*ay;
        vert(verts, BOTTOMRIGHT1, x + centerX, y);
        if (ax >= 0f) { // rotation on the rights half
            vert(verts, TOPLEFT1, x, y2);
            vert(verts, TOPRIGHT1, xc, y2);
            vert(verts, BOTTOMLEFT1, x, y);
            vert(verts, BOTTOMLEFT2, xc, yc);
            vert(verts, TOPLEFT2, xc, y2);
            if (txa < tya) { // rotation on the right side
                vert(verts, TOPRIGHT2, x2, y2);
                vert(verts, BOTTOMRIGHT2, x2, yc + ty);
                draw = 2;
            } else if (ay > 0f) { // rotation on the top side
                vert(verts, BOTTOMRIGHT2, xc + tx, y2);
                vert(verts, TOPRIGHT2, xc + tx * 0.5f, y2);
                draw = 2;
            } else { // rotation on the bottom side
                vert(verts, TOPRIGHT2, x2, y2);
                vert(verts, BOTTOMRIGHT2, x2, y);
                vert(verts, TOPLEFT3, xc, yc);
                vert(verts, TOPRIGHT3, x2, y);
                vert(verts, BOTTOMLEFT3, xc + tx, y);
                vert(verts, BOTTOMRIGHT3, xc + tx * 0.5f, y);
                draw = 3;
            }
        } else { // rotation on the left half
            vert(verts, TOPRIGHT1, x + centerX, y + centerY);
            if (txa < tya) { // rotation on the left side
                vert(verts, BOTTOMLEFT1, x, y);
                vert(verts, TOPLEFT1, x, yc + ty);
                draw = 1;
            } else if (ay < 0f) { // rotation on the bottom side
                vert(verts, TOPLEFT1, xc + tx, y);
                vert(verts, BOTTOMLEFT1, xc + tx * 0.5f, y);
                draw = 1;
            } else { // rotation on the top side
                vert(verts, TOPLEFT1, x, y2);
                vert(verts, BOTTOMLEFT1, x, y);
                vert(verts, BOTTOMRIGHT2, xc, yc);
                vert(verts, BOTTOMLEFT2, x, y2);
                vert(verts, TOPLEFT2, xc + tx * 0.5f, y2);
                vert(verts, TOPRIGHT2, xc + tx, y2);
                draw = 2;
            }
        }
        this.dirty = false;
    }

    public void draw(final Batch batch, final float x, final float y, float width, float height, final float angle) {
        if (width < 0) { scaleX = -1f; width = -width; }
        if (height < 0) { scaleY = -1f; height = -height; }
        calculate(x, y, width, height, angle, u1, v1, u2, v2);

        // Update color
        float batchColor = batch.getPackedColor();
        for (int i = 0; i < 12; i++) {
            verts[i * 5 + 2] = batchColor;
        }

        batch.draw(texture, verts, 0, 20*draw);
    }

    public void draw(final Batch batch, final float x, final float y, final float angle) {
        draw(batch, x, y, width, height, angle);
    }

    public void setOrigin(float x, float y) {
        if (originX == x && originY == y)
            return;
        originX = x;
        originY = y;
        dirty = true;
    }

    public void setScale(float x, float y) {
        if (scaleX == x && scaleY == y)
            return;
        scaleX = x;
        scaleY = y;
        dirty = true;
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        draw(batch, x, y, width, height, this.angle);
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(final float angle) {
        if (this.angle == angle)
            return;
        this.angle = angle;
        dirty = true;
    }

    private float leftWidth = 0;
    private float rightWidth = 0;
    private float topHeight = 0;
    private float bottomHeight = 0;
    private float minWidth = 0;
    private float minHeight = 0;

    @Override
    public float getLeftWidth() {
        return leftWidth;
    }

    @Override
    public void setLeftWidth(float leftWidth) {
        this.leftWidth = leftWidth;
    }

    @Override
    public float getRightWidth() {
        return rightWidth;
    }

    @Override
    public void setRightWidth(float rightWidth) {
        this.rightWidth = rightWidth;
    }

    @Override
    public float getTopHeight() {
        return topHeight;
    }

    @Override
    public void setTopHeight(float topHeight) {
        this.topHeight = topHeight;
    }

    @Override
    public float getBottomHeight() {
        return bottomHeight;
    }

    @Override
    public void setBottomHeight(float bottomHeight) {
        this.bottomHeight = bottomHeight;
    }

    @Override
    public float getMinWidth() {
        return minWidth;
    }

    @Override
    public void setMinWidth(float minWidth) {
        this.minWidth = minWidth;
    }

    @Override
    public float getMinHeight() {
        return minHeight;
    }

    @Override
    public void setMinHeight(float minHeight) {
        this.minHeight = minHeight;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTextureRegion(final TextureRegion textureRegion) {
        this.texture = textureRegion.getTexture();
        this.u1 = textureRegion.getU();
        this.v1 = textureRegion.getV();
        this.u2 = textureRegion.getU2();
        this.v2 = textureRegion.getV2();
        this.du = u2 - u1;
        this.dv = v2 - v1;
        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
        this.dirty = true;

        setMinWidth(textureRegion.getRegionWidth());
        setMinHeight(textureRegion.getRegionHeight());
    }
}
