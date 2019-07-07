package com.crashinvaders.vfx.common.lml.attributes;

import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlActorBuilder;
import com.github.czyzby.lml.parser.tag.LmlBuildingAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Generic placeholder attribute that can be used just to reserve specific attribute names in parser. */
public class DummyBuildingLmlAttribute implements LmlBuildingAttribute<LmlActorBuilder> {
    @Override
    public Class<LmlActorBuilder> getBuilderType() {
        return LmlActorBuilder.class;
    }

    @Override
    public boolean process(LmlParser parser, LmlTag tag, LmlActorBuilder builder, String rawAttributeData) {
        return true;
    }
}
