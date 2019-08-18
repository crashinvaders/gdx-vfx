/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.common.lml;

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
