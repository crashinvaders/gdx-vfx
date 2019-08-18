
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

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Allows to force image of a {@link ImageButton}. This attribute will copy button's style and change
 * {@link ImageButtonStyle#imageUp} - if this is the only image in the style, it will be always drawn on the
 * button. Mapped to "image", "icon".
 *
 * @author MJ */
public class ButtonCheckedImageLmlAttribute implements LmlAttribute<ImageButton> {
    @Override
    public Class<ImageButton> getHandledType() {
        return ImageButton.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final ImageButton actor,
                        final String rawAttributeData) {
        final ImageButtonStyle style = new ImageButtonStyle(actor.getStyle());
        style.imageChecked = parser.getData().getDefaultSkin().getDrawable(parser.parseString(rawAttributeData, actor));
        actor.setStyle(style);
    }
}
