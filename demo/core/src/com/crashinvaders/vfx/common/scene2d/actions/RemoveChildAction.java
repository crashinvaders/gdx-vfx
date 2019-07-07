package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class RemoveChildAction extends Action {

    private Actor child;
    private Group target;

    public void setChild(Actor child) {
        this.child = child;
    }

    @Override
    public void reset() {
        super.reset();
        child = null;
    }

    @Override
    public void setTarget(Actor target) {
        super.setTarget(target);
        this.target = (Group) target;
    }

    @Override
    public boolean act(float delta) {
        target.removeActor(child);
        return true;
    }
}
