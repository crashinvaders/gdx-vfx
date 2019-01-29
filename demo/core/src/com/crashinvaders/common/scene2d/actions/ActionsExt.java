package com.crashinvaders.common.scene2d.actions;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    public static Action unfocus(Actor actor) {
        return new UnfocusAction(actor);
    }

    public static class UnfocusAction extends Action {
        private final Actor actor;

        public UnfocusAction(Actor actor) {
            this.actor = actor;
        }

        @Override
        public boolean act(float delta) {
            Stage stage = actor.getStage();
            if (stage != null) {
                stage.unfocus(actor);
            }
            return true;
        }
    }

    /** @see PostAction */
    public static PostAction post(Action action) {
        return post(1, action);
    }

    /** @see PostAction */
    public static PostAction post(int skipFrames) {
        return post(skipFrames, null);
    }

    /** @see PostAction */
    public static PostAction post(int skipFrames, Action action) {
        PostAction postAction = Actions.action(PostAction.class);
        postAction.setAction(action);
        postAction.setSkipFrames(skipFrames);
        return postAction;
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

    public static OptionalAction optional(OptionalAction.Condition condition, Action action) {
        OptionalAction optionalAction = action(OptionalAction.class);
        optionalAction.setAction(action);
        optionalAction.setCondition(condition);
        return optionalAction;
    }

    public static MoveByPathAction moveByPath(Path<Vector2> path, float duration,
                                              Interpolation interpolation) {
        MoveByPathAction action = action(MoveByPathAction.class);
        action.setPath(path);
        action.setDuration(duration);
        action.setInterpolation(interpolation);
        return action;
    }
}
