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

package com.crashinvaders.vfx.demo.screens.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.lml.CommonLmlParserBuilder;
import com.crashinvaders.vfx.common.lml.CommonLmlSyntax;
import com.crashinvaders.vfx.common.lml.EmptyActorConsumer;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.demo.App;
import com.crashinvaders.vfx.demo.screens.demo.controllers.*;

public class DemoScreen extends ScreenAdapter {
    private static final Color clearColor = new Color(0x80ff80ff);

    private final AssetManager assets;
    private final Batch batch;
    private final Stage stage;
    private final CommonLmlParser lmlParser;
    private final ViewControllerManager viewControllers;

    public DemoScreen() {
        // Asset initialization.
        {
            assets = new AssetManager();
            assets.load("skin/uiskin.json", Skin.class, null);

            // Textures
            {
                TextureLoader.TextureParameter paramsRegular = new TextureLoader.TextureParameter();
                paramsRegular.minFilter = Texture.TextureFilter.Nearest;
                paramsRegular.magFilter = Texture.TextureFilter.Nearest;
                paramsRegular.wrapU = Texture.TextureWrap.ClampToEdge;
                paramsRegular.wrapV = Texture.TextureWrap.ClampToEdge;

                TextureLoader.TextureParameter paramsRepeat = new TextureLoader.TextureParameter();
                paramsRepeat.minFilter = Texture.TextureFilter.Nearest;
                paramsRepeat.magFilter = Texture.TextureFilter.Nearest;
                paramsRepeat.wrapU = Texture.TextureWrap.Repeat;
                paramsRepeat.wrapV = Texture.TextureWrap.Repeat;

                assets.load("gdx-vfx-logo.png", Texture.class, paramsRegular);
                assets.load("bg-scene-pattern.png", Texture.class, paramsRepeat);
                assets.load("bg-transparency-tile.png", Texture.class, paramsRegular);
            }

            assets.finishLoading();
        }

        batch = new SpriteBatch();
        stage = new Stage(new ExtendViewport(640f, 480f), batch);
        stage.addListener(new StageDebugInputListener());

        lmlParser = (CommonLmlParser)new CommonLmlParserBuilder()
                .syntax(new CommonLmlSyntax())
                .skin(assets.get("skin/uiskin.json", Skin.class))
                .action(":empty", new EmptyActorConsumer())
                .build();

        viewControllers = new ViewControllerManager(stage);
        viewControllers.add(new ScreenBackgroundViewController(lmlParser, assets));
        viewControllers.add(new VfxViewController(viewControllers, lmlParser));
        viewControllers.add(new CanvasContentViewController(viewControllers, lmlParser, assets));
        viewControllers.add(new EffectRosterViewController(viewControllers, lmlParser));
        viewControllers.add(new StatisticPanelViewController(viewControllers, lmlParser));

        Group sceneRoot = (Group)lmlParser.parseTemplate(
                Gdx.files.internal("lml/screen-demo/root.lml")).first();
        stage.addActor(sceneRoot);

        viewControllers.onViewCreated(sceneRoot);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        assets.dispose();
        viewControllers.dispose();
    }

    @Override
    public void show() {
        App.inst().getInput().addProcessor(stage);
    }

    @Override
    public void hide() {
        App.inst().getInput().removeProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewControllers.update(delta);
        stage.act(delta);
        stage.draw();
    }

    private class StageDebugInputListener extends InputListener {

        boolean stageDebug = false;

        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            switch (keycode) {
                case Input.Keys.F1: {
                    stage.setDebugAll(stageDebug = !stageDebug);
                    return true;
                }
                default:
                    return super.keyDown(event, keycode);
            }
        }
    }
}
