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

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.action.ActorConsumer;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

public class ImageDrawableLmlAttribute implements LmlAttribute<Image> {

    @Override
    public Class<Image> getHandledType() {
        return Image.class;
    }

    @Override
    public void process(LmlParser parser, LmlTag tag, Image image, String rawAttributeData) {
        ActorConsumer<Drawable, Image> action = (ActorConsumer<Drawable, Image>) parser.parseAction(rawAttributeData, image);
        if (action == null) {
            parser.throwError("Cannot find action: " + rawAttributeData);
            return;
        }

        Drawable drawable = action.consume(image);
        image.setDrawable(drawable);
    }
}
