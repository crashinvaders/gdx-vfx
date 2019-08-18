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
