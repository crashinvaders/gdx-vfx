package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;

public class OptionalAction extends DelegateAction {

    private Condition condition;

    private boolean firstRun = true;
    private boolean checkPassed = false;

    @Override
    public void restart() {
        super.restart();
        firstRun = true;
        checkPassed = false;
    }

    @Override
    public void reset() {
        super.reset();
        condition = null;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    protected boolean delegate(float delta) {
        if (firstRun) {
            checkPassed = condition.check();
            firstRun = false;
        }
        if (checkPassed) {
            return action.act(delta);
        } else {
            return true;
        }
    }

    public interface Condition {
        boolean check();
    }
}
