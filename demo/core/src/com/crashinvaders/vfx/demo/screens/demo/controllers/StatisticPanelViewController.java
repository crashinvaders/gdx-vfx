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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.viewcontroller.LmlViewController;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.scene2d.VfxWidgetGroup;
import com.github.czyzby.lml.annotation.LmlActor;

public class StatisticPanelViewController extends LmlViewController {

    @LmlActor("lblFboSize") Label lblFboSize;
    @LmlActor("lblFps") Label lblFps;

    private VfxManager vfxManager;;

    public StatisticPanelViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser) {
        super(viewControllers, lmlParser);
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        super.onViewCreated(sceneRoot);
        processLmlFields(this);

        VfxWidgetGroup vfxGroup = sceneRoot.findActor("vfxGroup");
        vfxManager = vfxGroup.getVfxManager();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        updateFpsView();
        updateFboSizeView();
    }

    public void updateFboSizeView() {
        lblFboSize.setText(vfxManager.getWidth() + "x" + vfxManager.getHeight());
    }

    public void updateFpsView() {
        int fps = Gdx.graphics.getFramesPerSecond();
        lblFps.setText(String.valueOf(fps));
    }
}