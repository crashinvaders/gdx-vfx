package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

/**
 * Skips N amount of frames before executes wrapped action.
 * Typical usecase is when you need execute action after parent actor has been laid out.
 */
public class PostAction extends DelegateAction {
    private int framesLeft;

    public void setSkipFrames(int skipFrames) {
        this.framesLeft = skipFrames;
    }

    @Override
    public void reset() {
        super.reset();
        framesLeft = 0;
    }

    @Override
    protected boolean delegate(float delta) {
        if (framesLeft > 0) {
            framesLeft--;
            return false;
        }

        if (action == null) return true;
        return action.act(delta);
    }
}
