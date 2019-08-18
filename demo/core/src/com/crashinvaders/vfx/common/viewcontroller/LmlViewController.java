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

package com.crashinvaders.vfx.common.viewcontroller;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.lml.LmlUtils;
import com.github.czyzby.lml.parser.LmlView;
import com.github.czyzby.lml.parser.action.ActionContainer;

public abstract class LmlViewController implements ViewController, LmlView, ActionContainer {

    protected final ViewControllerManager viewControllers;
    protected final CommonLmlParser lmlParser;
    protected final Stage stage;
    protected final Skin skin;

    protected Group sceneRoot;

    public LmlViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser) {
        this.viewControllers = viewControllers;
        this.lmlParser = lmlParser;
        this.stage = viewControllers.getStage();
        this.lmlParser.getData().addActionContainer(getViewId(), this);
        this.skin = this.lmlParser.getData().getDefaultSkin();
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        this.sceneRoot = sceneRoot;
    }

    @Override
    public void dispose() {
        this.lmlParser.getData().removeActionContainer(getViewId());
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public Stage getStage() {
        // Provide no stage to LmlParser in order to add actors manually.
        return null;
    }

    @Override
    public String getViewId() {
        // All LmlViewControllers are accessible from the LML markup by its class name by default.
        return this.getClass().getSimpleName();
    }

    protected <T extends Actor> T parseLmlTemplate(FileHandle fileHandle) {
        return LmlUtils.parseLmlTemplate(lmlParser, this, fileHandle);
    }

    protected <T extends ViewController> T getController(Class<T> type) {
        return viewControllers.get(type);
    }

    /** @see CommonLmlParser#processLmlFieldAnnotations(Object) */
    protected <View> void processLmlFields(View view) {
        lmlParser.processLmlFieldAnnotations(view);
    }
}
