
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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.parser.tag.LmlAttribute;
import com.github.czyzby.lml.parser.tag.LmlTag;

/** Turns {@link Slider} into vertical scroll bar.
 * In attribute data you should provide a {@link ScrollPane}'s ID tp be bound to. */
public class VerticalScrollSliderLmlAttribute implements LmlAttribute<Slider> {
    @Override
    public Class<Slider> getHandledType() {
        return Slider.class;
    }

    @Override
    public void process(final LmlParser parser, final LmlTag tag, final Slider slider, final String rawAttributeData) {
        Actor actor = parser.getActorsMappedByIds().get(rawAttributeData);
        if (actor == null) {
            parser.throwError("Can't find actor with ID: " + rawAttributeData);
        }
        if (!(actor instanceof ScrollPane)) {
            parser.throwError("Actor should be a ScrollPane");
        }
        final ScrollPane scrollPane = (ScrollPane) actor;

        scrollPane.addAction(Actions.forever(Actions.run(new Runnable() {
            @Override
            public void run() {
                boolean scrollRequired = scrollPane.getActor().getHeight() > scrollPane.getScrollHeight();
                slider.setVisible(scrollRequired);
                slider.setValue(MathUtils.lerp(
                        slider.getMinValue(),
                        slider.getMaxValue(),
                        1f - scrollPane.getVisualScrollPercentY()));
            }
        })));

        slider.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                scrollPane.cancel();
                scrollPane.fling(0f, 0f, 0f);
                scrollPane.setSmoothScrolling(false);
                updateScrollPaneFromSlider(y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updateScrollPaneFromSlider(y);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }

            private void updateScrollPaneFromSlider(float y) {
                float sliderPercentY = MathUtils.clamp(y / slider.getHeight(), 0f, 1f);;
                scrollPane.setScrollPercentY(1f - sliderPercentY);
            }
        });
    }
}
