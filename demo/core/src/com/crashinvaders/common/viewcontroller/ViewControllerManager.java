package com.crashinvaders.common.viewcontroller;

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
