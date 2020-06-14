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

package com.crashinvaders.vfx.demo.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.crashinvaders.vfx.demo.App;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

public class HtmlLauncher extends GwtApplication {

    private static final int PADDING = 0;
    private GwtApplicationConfiguration cfg;

    @Override
    public GwtApplicationConfiguration getConfig() {
        Window.enableScrolling(false);
        Window.setMargin("0");
        Window.addResizeHandler(new ResizeListener());

        int w = Window.getClientWidth() - PADDING;
        int h = Window.getClientHeight() - PADDING;
        cfg = new GwtApplicationConfiguration(w, h);
        cfg.preferFlash = false;
        cfg.disableAudio = true;
//        cfg.useDebugGL = true;  //TODO Remove it.
        return cfg;
    }

    class ResizeListener implements ResizeHandler {
        @Override
        public void onResize(ResizeEvent event) {
            int width = event.getWidth() - PADDING;
            int height = event.getHeight() - PADDING;
            getRootPanel().setSize(width + "px", height + "px");
            getApplicationListener().resize(width, height);
            Gdx.graphics.setWindowedMode(width, height);
        }
    }

    @Override
    public ApplicationListener createApplicationListener() {
        setLoadingListener(new LoadingListener() {
            @Override
            public void beforeSetup() {

            }

            @Override
            public void afterSetup() {
                Gdx.app.setApplicationLogger(new GwtConsoleLogger());
                Gdx.app.setLogLevel(LOG_DEBUG);
            }
        });

        return new App();
    }
}