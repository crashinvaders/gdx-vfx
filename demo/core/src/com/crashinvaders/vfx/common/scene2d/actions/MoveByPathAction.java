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

import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

/** Moves an actor from its current position to a specific position through the path. */
public class MoveByPathAction extends TemporalAction {
    private static final Vector2 tmpVec2 = new Vector2();

    private Path<Vector2> path;

    protected void update (float percent) {
        Vector2 pos = path.valueAt(tmpVec2, percent);
        actor.setPosition(pos.x, pos.y);
    }

    public void reset () {
        super.reset();
        path = null;
    }

    public void setPath(Path<Vector2> path) {
        this.path = path;
    }
}
