package com.crashinvaders.vfx.common.lml;

import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.impl.AbstractLmlParser;
import com.github.czyzby.lml.util.LmlParserBuilder;

public class CommonLmlParserBuilder extends LmlParserBuilder {

    public CommonLmlParserBuilder() {
        super();
    }

    public CommonLmlParserBuilder(LmlData lmlData) {
        super(lmlData);
    }

    @Override
    protected AbstractLmlParser getInstanceOfParser(LmlData lmlData) {
        return new CommonLmlParser(lmlData);
    }
}
