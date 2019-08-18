
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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Array in order x;y;width;height */
public class BoundsLmlAttribute implements LmlAttribute<Actor> {
    @Override
    public Class<Actor> getHandledType() {
        return Actor.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Actor actor, final String rawAttributeData) {
        String[] values = parser.parseArray(rawAttributeData, actor);
        if (values.length != 4) {
            parser.throwError("There must be exactly 4 values in the array");
        }
        try {
            actor.setBounds(
                    Float.parseFloat(values[0]),
                    Float.parseFloat(values[1]),
                    Float.parseFloat(values[2]),
                    Float.parseFloat(values[3]));
        } catch (NumberFormatException e) {
            parser.throwError("Can't read bounds values from the array.", e);
        }
    }
}
