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

package com.crashinvaders.vfx.scene2d;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.VfxManager;

public class VfxWidgetGroup extends WidgetGroup {

    private final VfxManager vfxManager;
    private boolean initialized = false;
    private boolean resizePending = false;

    public VfxWidgetGroup(Pixmap.Format pixelFormat) {
        vfxManager = new VfxManager(pixelFormat);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);

        if (stage != null) {
            initialize();
        } else {
            reset();
        }
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        resizePending = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        if (resizePending) {
            resizePending = false;
            vfxManager.resize(
                    MathUtils.floor(getWidth()),
                    MathUtils.floor(getHeight()));
        }
        vfxManager.beginCapture();
        batch.begin();
        super.draw(batch, parentAlpha);
        batch.end();
        vfxManager.endCapture();
        vfxManager.render();

        batch.begin();
    }

    public VfxManager getVfxManager() {
        return vfxManager;
    }

    private void initialize() {
        if (initialized) return;

        int width = (int)getWidth();
        int height = (int)getHeight();
        if (width == 0 || height == 0) {
            Viewport viewport = getStage().getViewport();
            width = MathUtils.floor(viewport.getWorldWidth());
            height = MathUtils.floor(viewport.getWorldHeight());
        }
        vfxManager.resize(width, height);
        resizePending = false;
        initialized = true;
    }

    private void reset() {
        if (!initialized) return;

        vfxManager.dispose();
        resizePending = false;
        initialized = false;
    }
}
