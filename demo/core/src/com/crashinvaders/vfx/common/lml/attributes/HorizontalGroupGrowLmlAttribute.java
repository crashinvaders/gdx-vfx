package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class HorizontalGroupGrowLmlAttribute implements LmlAttribute<HorizontalGroup> {
    @Override
    public Class<HorizontalGroup> getHandledType() {
        return HorizontalGroup.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, HorizontalGroup actor, String rawAttributeData) {
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
