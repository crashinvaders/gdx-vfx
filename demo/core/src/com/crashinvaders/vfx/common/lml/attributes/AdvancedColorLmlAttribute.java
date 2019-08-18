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

package com.crashinvaders.vfx.common.lml.attributes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.crashinvaders.vfx.utils.CommonUtils;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.attribute.ColorLmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/**
 * Extends {@link ColorLmlAttribute} with extra options.
 * 1. A HEX value could be used to describe a color. Available patterns are: #RGB, #RGBA, #RRGGBB, #RRGGBBAA.
 */
public class AdvancedColorLmlAttribute extends ColorLmlAttribute {

    private static final Color tmpColor = new Color();

    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        if (rawAttributeData.startsWith("#")) {
            String hexCode = rawAttributeData.substring(1);
            Color color;
            try {
                color = CommonUtils.parseHexColor(hexCode);
            } catch (Exception e) {
                parser.throwError("Error parsing HEX code value \"" + hexCode + "\"", e);
                return;
            }
            actor.setColor(color);
        } else {
            super.process(parser, tag, actor, rawAttributeData);
        }
    }
}
