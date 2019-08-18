/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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
