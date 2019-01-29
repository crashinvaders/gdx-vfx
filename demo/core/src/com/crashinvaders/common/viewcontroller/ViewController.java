package com.crashinvaders.common.viewcontroller;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

public interface ViewController extends Disposable {
    void onViewCreated(Group sceneRoot);
    void dispose();
    void update(float delta);
}
