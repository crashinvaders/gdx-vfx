package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

/** Multiplies delta time by timeFactor value for wrapped action. */
public class TimeModulationAction extends DelegateAction {

    private float timeFactor = 1f;

    @Override
    public void reset() {
        super.reset();
        timeFactor = 1f;
    }

    public float getTimeFactor() {
        return timeFactor;
    }

    public void setTimeFactor(float timeFactor) {
        this.timeFactor = timeFactor;
    }

    @Override
    protected boolean delegate(float delta) {
        return action.act(delta * timeFactor);
    }
}
