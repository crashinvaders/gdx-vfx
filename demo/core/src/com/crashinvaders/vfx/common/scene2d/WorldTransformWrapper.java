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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class WorldTransformWrapper extends Group {
    private final Actor actor;
    private final float upp;

    public WorldTransformWrapper(Actor actor, float upp) {
        this.actor = actor;
        this.addActor(actor);
        this.upp = upp;

        this.setSize(actor.getWidth() * upp, actor.getHeight() * upp);
        super.setTransform(true);
    }

    public <T extends Actor> T getActor() {
        return (T) actor;
    }

    @Override
    public void setScaleX(float scaleX) {
        super.setScaleX(scaleX * upp);
    }

    @Override
    public void setScaleY(float scaleY) {
        super.setScaleY(scaleY * upp);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY * upp);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        super.setScale(scaleX * upp, scaleY * upp);
    }

    @Override
    public void scaleBy(float scale) {
        super.setScale(
                (getScaleX() / upp + scale) * upp,
                (getScaleY() / upp + scale) * upp);
    }

    @Override
    public void scaleBy(float scaleX, float scaleY) {
        super.setScale(
                (getScaleX() / upp + scaleX) * upp,
                (getScaleY() / upp + scaleY) * upp);
    }

    @Deprecated
    @Override
    public void setTransform(boolean transform) {
        throw new UnsupportedOperationException("Transform is always enabled for this actor.");
    }
}
