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

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.impl.attribute.container.AbstractSharedContainerAndCellLmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;
import com.github.czyzby.lml.util.LmlUtilities;

public class ContainerPadRightLmlAttribute extends AbstractSharedContainerAndCellLmlAttribute {
    @Override
    protected void applyToContainer(final LmlParser parser, final LmlTag tag, final Container<?> actor,
                                    final String rawAttributeData) {
        actor.padRight(LmlUtilities.parseHorizontalValue(parser, tag.getParent(), actor, rawAttributeData));
    }
    @Override
    protected void applyToCell(final Container<?> actor, final Cell<?> cell) {
        cell.padRight(actor.getPadRight()); // Any could do, height(Value) sets all.
    }
}
