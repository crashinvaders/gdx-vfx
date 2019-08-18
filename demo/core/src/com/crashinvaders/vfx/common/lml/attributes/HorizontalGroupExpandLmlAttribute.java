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

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class HorizontalGroupExpandLmlAttribute implements LmlAttribute<HorizontalGroup> {
    @Override
    public Class<HorizontalGroup> getHandledType() {
        return HorizontalGroup.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, HorizontalGroup actor, String rawAttributeData) {
        actor.expand(parser.parseBoolean(rawAttributeData));
    }
}
