package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.DelegateAction;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.utils.Array;

public class CustomTargetAction extends DelegateAction {
    @Override
    public void setTarget(Actor target) {
        this.target = target;

        recursivelyUpdateTarget(getAction(), target);
    }



    private void recursivelyUpdateTarget(Action action, Actor target) {
        if (action instanceof CustomTargetAction) return;

        action.setTarget(target);

        if (action instanceof DelegateAction) {
            DelegateAction delegateAction = (DelegateAction) action;
            Action wrappedAction = delegateAction.getAction();
            if (!(wrappedAction instanceof CustomTargetAction)) {
                recursivelyUpdateTarget(wrappedAction, target);
            }

        } else if (action instanceof ParallelAction) {
            ParallelAction parallelAction = (ParallelAction) action;
            Array<Action> actions = parallelAction.getActions();
            for (int i = 0; i < actions.size; i++) {
                Action childAction = actions.get(i);
                if (!(childAction instanceof CustomTargetAction)) {
                    recursivelyUpdateTarget(childAction, target);
                }
            }
        }
    }

    @Override
    protected boolean delegate(float delta) {
        return action.act(delta);
    }
}
