package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.crashinvaders.common.lml.CommonLmlParser;
import com.crashinvaders.common.viewcontroller.LmlViewController;
import com.crashinvaders.common.viewcontroller.ViewControllerManager;

public class CanvasContentViewController extends LmlViewController {

    private final AssetManager assets;

    private WidgetGroup canvasRoot;

    public CanvasContentViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser, AssetManager assets) {
        super(viewControllers, lmlParser);
        this.assets = assets;
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        super.onViewCreated(sceneRoot);
        canvasRoot = sceneRoot.findActor("canvasRoot");

        Texture texture = assets.get("gdx-vfx.png");
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Image image = new Image(texture);
        image.setOrigin(Align.center);
        Container<Image> container = new Container<>(image);
        container.setFillParent(true);
        canvasRoot.addActor(container);

        image.addAction(Actions.forever(Actions.parallel(
                Actions.rotateBy(360f, 3f),
                Actions.sequence(
                        Actions.scaleTo(1.25f, 1.25f),
                        Actions.scaleTo(0.75f, 0.75f, 1.5f, Interpolation.pow2),
                        Actions.scaleTo(1.25f, 1.25f, 1.5f, Interpolation.pow2))
        )));
    }
}
