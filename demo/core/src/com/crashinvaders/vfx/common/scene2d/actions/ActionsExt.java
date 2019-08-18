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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.action;

public final class ActionsExt {

    private ActionsExt() { }

    /** Assign custom target actor. It will also set custom target to all descendant actions. */
    public static Action target(Actor target, Action wrappedAction) {
        CustomTargetAction action = Actions.action(CustomTargetAction.class);
        action.setAction(wrappedAction);
        action.setTarget(target);
        return action;
    }

    /** @see PostAction */
    public static PostAction post(Action wrappedAction) {
        return post(1, wrappedAction);
    }

    /** @see PostAction */
    public static PostAction post(int skipFrames) {
        return post(skipFrames, null);
    }

    /** @see PostAction */
    public static PostAction post(int skipFrames, Action wrappedAction) {
        PostAction action = Actions.action(PostAction.class);
        action.setAction(wrappedAction);
        action.setSkipFrames(skipFrames);
        return action;
    }

    /** Calls {@link Actor#act(float)} */
    public static ActAction act(float delta) {
        ActAction action = action(ActAction.class);
        action.setDelta(delta);
        return action;
    }

    /**
     * Can only be assigned to {@link Group} actor
     * <br/>
     * Calls {@link Group#setTransform(boolean)}
     */
    public static TransformAction transform(boolean transform) {
        TransformAction action = action(TransformAction.class);
        action.setTransform(transform);
        return action;
    }

    /** @see UnfocusAction */
    public static Action unfocus() {
        UnfocusAction action = action(UnfocusAction.class);
        return action;
    }

    public static OriginAlignAction origin(int align) {
        OriginAlignAction action = action(OriginAlignAction.class);
        action.setAlign(align);
        return action;
    }

    public static RemoveChildAction removeChild(Actor child) {
        RemoveChildAction action = action(RemoveChildAction.class);
        action.setChild(child);
        return action;
    }

    public static OptionalAction optional(OptionalAction.Condition condition, Action wrappedAction) {
        OptionalAction action = action(OptionalAction.class);
        action.setAction(wrappedAction);
        action.setCondition(condition);
        return action;
    }

    public static MoveByPathAction moveByPath(Path<Vector2> path, float duration, Interpolation interpolation) {
        MoveByPathAction action = action(MoveByPathAction.class);
        action.setPath(path);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }

    /** @see TimeModulationAction */
    public static TimeModulationAction timeModulation(Action wrappedAction) {
        return timeModulation(1f, wrappedAction);
    }

    /** @see TimeModulationAction */
    public static TimeModulationAction timeModulation(float timeFactor, Action wrappedAction) {
        TimeModulationAction action = action(TimeModulationAction.class);
        action.setAction(wrappedAction);
        action.setTimeFactor(timeFactor);
        return action;
    }
}
