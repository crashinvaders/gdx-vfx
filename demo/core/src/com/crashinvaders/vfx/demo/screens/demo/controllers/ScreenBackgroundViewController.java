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

package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.viewcontroller.ViewController;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.github.czyzby.lml.annotation.LmlAction;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActionContainer;

public class ScreenBackgroundViewController implements ViewController, ActionContainer {
    private static final String TAG = ScreenBackgroundViewController.class.getSimpleName();

    private final LmlParser lmlParser;
    private final AssetManager assets;

    public ScreenBackgroundViewController(LmlParser lmlParser, AssetManager assets) {
        this.lmlParser = lmlParser;
        this.assets = assets;
        lmlParser.getData().addActionContainer(TAG, this);
    }

    @Override
    public void onViewCreated(Group sceneRoot) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {

    }

    @LmlAction("createCheckerboardDrawable") Drawable createCheckerboardDrawable() {
        Texture texture = assets.get("bg-transparency-tile.png");
        return new TiledDrawable(new TextureRegion(texture));
    }
}
