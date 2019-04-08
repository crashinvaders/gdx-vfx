package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class CheckBoxTextSpaceLmlAttribute implements LmlAttribute<CheckBox> {
    @Override
    public Class<CheckBox> getHandledType() {
        return CheckBox.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final CheckBox actor, final String rawAttributeData) {
        int space = parser.parseInt(rawAttributeData, actor);
        actor.getLabelCell().padLeft(space);
    }
}
