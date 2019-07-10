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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.lml.CommonLmlParserBuilder;
import com.crashinvaders.vfx.common.lml.CommonLmlSyntax;
import com.crashinvaders.vfx.common.lml.EmptyActorConsumer;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.demo.App;
import com.crashinvaders.vfx.demo.screens.demo.controllers.CanvasContentViewController;
import com.crashinvaders.vfx.demo.screens.demo.controllers.EffectRosterViewController;
import com.crashinvaders.vfx.demo.screens.demo.controllers.PostProcessorViewController;

public class TestScreen extends ScreenAdapter {
    private static final Color clearColor = new Color(0x80a050ff);

    private final AssetManager assets;
    private final Batch batch;
    private final Stage stage;

    public TestScreen() {
        // Asset initialization.
        {
            assets = new AssetManager();
            assets.load("skin/uiskin.json", Skin.class, null);
            assets.finishLoading();
        }

        batch = new SpriteBatch();
        stage = new Stage(new ExtendViewport(640f, 480f), batch);
        stage.addListener(new StageDebugInputListener());

        {
            Label label = new Label("Hello there!", assets.get("skin/uiskin.json", Skin.class));
            Container container = new Container<>(label);
            container.setFillParent(true);
            container.align(Align.center);
            stage.addActor(container);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        assets.dispose();
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
