package com.crashinvaders.common.scene2d.actions;

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
