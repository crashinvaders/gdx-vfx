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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActAction extends Action {

    private float delta;

    public void setDelta(float delta) {
        this.delta = delta;
    }

    @Override
    public void reset() {
        super.reset();
        delta = 0f;
    }

    @Override
    public boolean act(float delta) {
        final Actor target = getTarget();
        final float timeDelta = this.delta;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                target.act(timeDelta);
            }
        });
        return true;
    }
}
