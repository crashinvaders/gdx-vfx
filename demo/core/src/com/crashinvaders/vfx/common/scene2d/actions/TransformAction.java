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

/** Changes group's transform property, see {@link Group#setTransform(boolean)} for details. */
public class TransformAction extends Action {
    private boolean transform;
    private Group targetGroup;

    @Override
    public void setTarget(Actor target) {
        super.setTarget(target);
        if (target != null) {
            if (!(target instanceof Group)) {
                throw new IllegalStateException("TransformAction can be assigned only to a Group");
            }
            targetGroup = (Group) target;
        }
    }

    public boolean act(float delta) {
        targetGroup.setTransform(transform);
        return true;
    }

    public boolean isTransform() {
        return transform;
    }

    public void setTransform(boolean transform) {
        this.transform = transform;
    }

    public void reset() {
        super.reset();
        targetGroup = null;
        transform = false;
    }
}
