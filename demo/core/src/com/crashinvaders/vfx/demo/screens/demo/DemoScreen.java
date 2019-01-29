package com.crashinvaders.vfx.demo.screens.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
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
import com.crashinvaders.common.lml.CommonLmlParser;
import com.crashinvaders.common.lml.CommonLmlParserBuilder;
import com.crashinvaders.common.lml.CommonLmlSyntax;
import com.crashinvaders.common.lml.EmptyActorConsumer;
import com.crashinvaders.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.demo.App;
import com.crashinvaders.vfx.demo.screens.demo.controllers.CanvasContentViewController;
import com.crashinvaders.vfx.demo.screens.demo.controllers.EffectRosterViewController;
import com.crashinvaders.vfx.demo.screens.demo.controllers.PostProcessorViewController;

public class DemoScreen extends ScreenAdapter {
    private static final Color clearColor = new Color(0x808080ff);

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
            assets.load("gdx-vfx.png", Texture.class, null);
            assets.finishLoading();
        }

        batch = new SpriteBatch();
        stage = new Stage(new ExtendViewport(640f, 480f), batch);
        stage.addListener(new StageDebugInputListener());

        lmlParser = (CommonLmlParser)new CommonLmlParserBuilder()
                .syntax(new CommonLmlSyntax())
                .skin(assets.get("skin/uiskin.json", Skin.class))
//                .i18nBundle(App.inst().getI18n().getDefaultBundle())
                .action(":empty", new EmptyActorConsumer())
                .build();

        viewControllers = new ViewControllerManager(stage);
        viewControllers.add(new PostProcessorViewController(viewControllers, lmlParser));
        viewControllers.add(new CanvasContentViewController(viewControllers, lmlParser, assets));
        viewControllers.add(new EffectRosterViewController(viewControllers, lmlParser));

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
