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
