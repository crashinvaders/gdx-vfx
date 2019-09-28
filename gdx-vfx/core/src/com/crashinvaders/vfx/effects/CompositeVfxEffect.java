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

package com.crashinvaders.vfx.effects;

import com.badlogic.gdx.utils.Array;

/**
 * Base class for the effects that are a composition of other effects.
 * The class manages contained effects and delegates the lifecycle methods to the instances (e.g. resize, rebind, update, dispose).
 * To register an internal effect, call {@link #register(VfxEffect)}.
 */
public abstract class CompositeVfxEffect extends AbstractVfxEffect {

    protected final Array<VfxEffect> managedEffects = new Array<>();

    @Override
    public void resize(int width, int height) {
        for (int i = 0; i < managedEffects.size; i++) {
            managedEffects.get(i).resize(width, height);
        }
    }

    @Override
    public void rebind() {
        for (int i = 0; i < managedEffects.size; i++) {
            managedEffects.get(i).rebind();
        }
    }

    @Override
    public void update(float delta) {
        for (int i = 0; i < managedEffects.size; i++) {
            managedEffects.get(i).update(delta);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < managedEffects.size; i++) {
            managedEffects.get(i).dispose();
        }
    }

    protected <T extends VfxEffect> T register(T effect) {
        managedEffects.add(effect);
        return effect;
    }

    protected <T extends VfxEffect> T unregister(T effect) {
        managedEffects.removeValue(effect, true);
        return effect;
    }
}
