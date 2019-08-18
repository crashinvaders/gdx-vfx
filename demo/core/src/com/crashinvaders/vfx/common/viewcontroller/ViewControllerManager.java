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

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;

public class ViewControllerManager implements Disposable {

    private final ArrayMap<Class<? extends ViewController>, ViewController> viewControllers = new ArrayMap<>();
    private final Stage stage;

    private boolean viewCreated = false;
    private Group sceneRoot = null;

    public ViewControllerManager(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void dispose() {
        for (int i = 0; i < viewControllers.size; i++) {
            viewControllers.getValueAt(i).dispose();
        }
        viewControllers.clear();
    }

    public void update(float delta) {
        for (int i = 0; i < viewControllers.size; i++) {
            viewControllers.getValueAt(i).update(delta);
        }
    }

    public void add(ViewController viewController) {
        viewControllers.put(viewController.getClass(), viewController);

        if (viewCreated) {
            viewController.onViewCreated(sceneRoot);
        }
    }

    public void remove(ViewController viewController) {
        viewControllers.removeKey(viewController.getClass());

        if (viewCreated) {
            viewController.dispose();
        }
    }

    public <T extends ViewController> T get(Class<T> viewControllerType) {
        return (T)viewControllers.get(viewControllerType);
    }

    public void onViewCreated(Group sceneRoot) {
        if (viewCreated) {
            throw new IllegalStateException("View controller already has been initialized.");
        }

        this.sceneRoot = sceneRoot;
        for (int i = 0; i < viewControllers.size; i++) {
            viewControllers.getValueAt(i).onViewCreated(sceneRoot);
        }
    }

    public Stage getStage() {
        return stage;
    }

    public Group getSceneRoot() {
        return sceneRoot;
    }
}
