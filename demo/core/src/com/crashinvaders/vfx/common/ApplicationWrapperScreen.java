package com.crashinvaders.vfx.common;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Screen;

/** A simple wrapper around {@link ApplicationListener} instance. */
public class ApplicationWrapperScreen implements Screen {

    private final ApplicationListener application;

    public ApplicationWrapperScreen(ApplicationListener application) {
        this.application = application;
        application.create();
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {
        application.pause();
    }

    @Override
    public void resume() {
        application.resume();
    }

    @Override
    public void render(float delta) {
        application.render();
    }

    @Override
    public void resize(int width, int height) {
        application.resize(width, height);
    }

    @Override
    public void dispose() {
        application.dispose();
    }
}
