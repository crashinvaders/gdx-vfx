package com.crashinvaders.vfx.scene2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;

/**
 * Stretches the actor to fill the area and rounds its size to an integer value (round by floor).
 *
 * @author metaphore
 */
public class IntegerRoundFillContainer extends WidgetGroup {
    private final Actor actor;

    public IntegerRoundFillContainer(Actor actor) {
        this.actor = actor;
        addActor(actor);
        setTransform(false);
    }

    @Override
    public void layout() {
        super.layout();
        actor.setPosition(0f, 0f);
        actor.setSize(MathUtils.floor(getWidth()), MathUtils.floor(getHeight()));
    }

    @Override
    public float getPrefWidth() {
        float prefWidth = actor.getWidth();
        if (actor instanceof Layout) {
            Layout layout = (Layout) actor;
            prefWidth = layout.getPrefWidth();
        }
        return MathUtils.floor(prefWidth);
    }

    @Override
    public float getPrefHeight() {
        float prefHeight = actor.getHeight();
        if (actor instanceof Layout) {
            Layout layout = (Layout) actor;
            prefHeight = layout.getPrefHeight();
        }
        return MathUtils.floor(prefHeight);
    }
}
