package com.crashinvaders.vfx.common.scene2d.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Unfocus target actor.
 */
public class UnfocusAction extends Action {
    @Override
    public boolean act(float delta) {
        Stage stage = target.getStage();
        if (stage != null) {
            stage.unfocus(target);
        }
        return true;
    }
}
