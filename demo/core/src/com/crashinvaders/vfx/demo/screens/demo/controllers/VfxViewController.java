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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.viewcontroller.LmlViewController;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.scene2d.VfxWidgetGroup;
import com.github.czyzby.lml.annotation.LmlAction;

public class VfxViewController extends LmlViewController {

    private VfxManager vfxManager;
    private WidgetGroup canvasRoot;

    public VfxViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser) {
        super(viewControllers, lmlParser);
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }

    public Group getCanvasRoot() {
        return canvasRoot;
    }

    @LmlAction("createCanvas") Actor createCanvas() {
        canvasRoot = new WidgetGroup();
        canvasRoot.setName("canvasRoot");
        canvasRoot.setFillParent(true);

        VfxWidgetGroup vfxGroup = new VfxWidgetGroup(Pixmap.Format.RGBA8888);
        vfxGroup.setName("vfxGroup");
        vfxGroup.addActor(canvasRoot);
        vfxGroup.setMatchWidgetSize(true);

        vfxManager = vfxGroup.getVfxManager();
        vfxManager.setBlendingEnabled(false);

        return vfxGroup;
    }
}
