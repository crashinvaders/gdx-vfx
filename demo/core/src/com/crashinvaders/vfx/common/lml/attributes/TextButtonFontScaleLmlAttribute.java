
package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class TextButtonFontScaleLmlAttribute implements LmlAttribute<TextButton> {
    @Override
    public Class<TextButton> getHandledType() {
        return TextButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final TextButton actor, final String rawAttributeData) {
        actor.getLabel().setFontScale(parser.parseFloat(rawAttributeData, actor));
    }
}
