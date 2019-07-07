package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.scene2d.RepeatTextureDrawable;
import com.crashinvaders.vfx.common.scene2d.actions.ActionsExt;
import com.crashinvaders.vfx.common.viewcontroller.LmlViewController;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;

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

        // Background
        {
            final RepeatTextureDrawable backgroundDrawable = new RepeatTextureDrawable(
                    assets.get("bg-pattern.png", Texture.class));
            backgroundDrawable.setShift(0.0f, 0.0f);
            Image imgBackground = new Image(backgroundDrawable);
            imgBackground.setFillParent(true);
//            imgBackground.setScale(3f);
            canvasRoot.addActor(imgBackground);

            imgBackground.addAction(new Action() {
                private static final float SPEED_MIN = 0.1f;
                private static final float SPEED_MAX = 0.3f;
                private static final float SPEED_DELTA = SPEED_MAX - SPEED_MIN;

                float progress;
                float speedX;
                float speedY;
                float shiftFactorX;
                float shiftFactorY;

                @Override
                public boolean act(float delta) {
                    progress = (progress + 0.3f * delta) % 1f;

                    float progressX = Math.abs(progress * 2f - 1f);
                    speedX = SPEED_MIN + progressX * SPEED_DELTA;

                    float progressY = Math.abs(((progress + 0.5f) % 1f) * 2f - 1f);
                    speedY = SPEED_MIN + progressY * SPEED_DELTA;

                    shiftFactorX -= speedX * delta;
                    shiftFactorY += speedY * delta;

                    backgroundDrawable.setShift(shiftFactorX, shiftFactorY);
                    return false;
                }
            });
        }

        // Logo
        {
            Texture texture = assets.get("gdx-vfx-logo.png");
            Image imageLogo = new Image(texture);
            imageLogo.setOrigin(Align.center);
            // Wrap into first container to setup size.
            Container containerImage = new Container<>(imageLogo);
//            containerImage.size(308f, 252f);
            // Wrap into an another container to always keep composition at center of the screen.
            Container containerHolder = new Container<>(containerImage);
            containerHolder.setFillParent(true);
            canvasRoot.addActor(containerHolder);

            imageLogo.addAction(ActionsExt.post(Actions.sequence(
                    ActionsExt.origin(Align.center),
                    Actions.moveBy(-100f, -50f),
                    Actions.parallel(
                            Actions.forever(Actions.sequence(
                                    Actions.rotateBy(720f, 4f, Interpolation.pow3),
                                    Actions.rotateBy(-720f, 4f, Interpolation.pow3))),
                            Actions.forever(Actions.sequence(
                                    Actions.moveBy(+200f, +100f, 0.8f, Interpolation.sine),
                                    Actions.moveBy(-200f, -100f, 0.8f, Interpolation.sine))),
                            Actions.forever(Actions.sequence(
                                    Actions.scaleTo(1.25f, 1.25f),
                                    Actions.scaleTo(0.75f, 0.75f, 1.5f, Interpolation.pow2),
                                    Actions.scaleTo(1.25f, 1.25f, 1.5f, Interpolation.pow2)))
                    )
            )));
        }
    }
}
