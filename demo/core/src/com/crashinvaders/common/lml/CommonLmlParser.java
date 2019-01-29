package com.crashinvaders.common.lml;

import com.github.czyzby.lml.parser.LmlData;
import com.github.czyzby.lml.parser.LmlStyleSheet;
import com.github.czyzby.lml.parser.LmlSyntax;
import com.github.czyzby.lml.parser.LmlTemplateReader;
import com.github.czyzby.lml.parser.impl.DefaultLmlParser;

/** Has few extra methods that provide public access for some {@link DefaultLmlParser}'s protected methods. */
public class CommonLmlParser extends DefaultLmlParser {

    public CommonLmlParser(LmlData data) {
        super(data);
    }

    public CommonLmlParser(LmlData data, LmlSyntax syntax) {
        super(data, syntax);
    }

    public CommonLmlParser(LmlData data, LmlSyntax syntax, LmlTemplateReader templateReader) {
        super(data, syntax, templateReader);
    }

    public CommonLmlParser(LmlData data, LmlSyntax syntax, LmlTemplateReader templateReader, LmlStyleSheet styleSheet) {
        super(data, syntax, templateReader, styleSheet);
    }

    public CommonLmlParser(LmlData data, LmlSyntax syntax, LmlTemplateReader templateReader, boolean strict) {
        super(data, syntax, templateReader, strict);
    }

    public CommonLmlParser(LmlData data, LmlSyntax syntax, LmlTemplateReader templateReader, LmlStyleSheet styleSheet, boolean strict) {
        super(data, syntax, templateReader, styleSheet, strict);
    }

    /** Public assess for protected {@link #processViewFieldAnnotations(Object)} method. */
    public <View> void processLmlFieldAnnotations(View view) {
        processViewFieldAnnotations(view);
    }
}
