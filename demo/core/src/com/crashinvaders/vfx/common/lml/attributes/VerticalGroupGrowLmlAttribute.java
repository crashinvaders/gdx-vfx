package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class VerticalGroupGrowLmlAttribute implements LmlAttribute<VerticalGroup> {
    @Override
    public Class<VerticalGroup> getHandledType() {
        return VerticalGroup.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, VerticalGroup actor, String rawAttributeData) {
        boolean grow = parser.parseBoolean(rawAttributeData);
        if (grow) {
            actor.expand(true);
            actor.fill(1.0f);
        } else {
            actor.expand(false);
            actor.fill(0.0f);
        }
    }
}
