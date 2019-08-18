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
import com.badlogic.gdx.scenes.scene2d.ui.Container;

/**
 * Same old {@link Container}, but gets shrinked when invisible
 */
public class ShrinkContainer<T extends Actor> extends Container<T> {

    public ShrinkContainer() {
        super();
    }

    public ShrinkContainer(T actor) {
        super(actor);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        invalidateHierarchy();
    }

    @Override
    public float getPrefWidth() {
        if (!isVisible()) return 0f;
        return super.getPrefWidth();
    }

    @Override
    public float getPrefHeight() {
        if (!isVisible()) return 0f;
        return super.getPrefHeight();
    }

    @Override
    public float getMinWidth() {
        if (!isVisible()) return 0f;
        return super.getMinWidth();
    }

    @Override
    public float getMinHeight() {
        if (!isVisible()) return 0f;
        return super.getMinHeight();
    }
}
