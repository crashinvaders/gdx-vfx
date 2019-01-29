
package com.crashinvaders.common.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Array in order x;y */
public class PositionLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        String[] values = parser.parseArray(rawAttributeData, actor);
        if (values.length != 2) {
            parser.throwError("There must be exactly 2 values in the array");
        }
        try {
            actor.setPosition(
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]));
        } catch (NumberFormatException e) {
            parser.throwError("Can't read position values from the array.", e);
        }
    }
}
