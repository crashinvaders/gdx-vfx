package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class ZIndexLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        final int zIndex = parser.parseInt(rawAttributeData, actor);
        Gdx.app.postRunnable(new Runnable() {
            @Override public void run() {
                actor.setZIndex(zIndex);
            }
        });
    }
}
