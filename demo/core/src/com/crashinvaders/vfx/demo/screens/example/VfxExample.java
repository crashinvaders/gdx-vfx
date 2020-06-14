package com.crashinvaders.vfx.demo.screens.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

public class VfxExample extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private VfxManager vfxManager;
    private GaussianBlurEffect vfxEffect;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        // VfxManager is a host for the effects.
        // It captures rendering into internal off-screen buffer and applies a chain of defined effects.
        // Off-screen buffers may have any pixel format, for this example we will use RGBA8888.
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

        // Create and add an effect.
        // VfxEffect derivative classes serve as controllers for the effects.
        // They provide public properties to configure and control them.
        vfxEffect = new GaussianBlurEffect();
        vfxManager.addEffect(vfxEffect);
    }

    @Override
    public void resize(int width, int height) {
        // VfxManager manages internal off-screen buffers,
        // which should always match the required viewport (whole screen in our case).
        vfxManager.resize(width, height);

        shapeRenderer.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
        shapeRenderer.updateMatrices();
    }

    @Override
    public void render() {
        // Clean up the screen.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Clean up internal buffers, as we don't need any information from the last render.
        vfxManager.cleanUpBuffers();

        // Begin render to an off-screen buffer.
        vfxManager.beginInputCapture();

        // Here's where game render should happen.
        // For demonstration purposes we just render some simple geometry.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(250f, 100f, 250f, 175f);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(200f, 250f, 100f);
        shapeRenderer.end();

        // End render to an off-screen buffer.
        vfxManager.endInputCapture();

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        vfxManager.applyEffects();

        // Render result to the screen.
        vfxManager.renderToScreen();
    }

    @Override
    public void dispose() {
        // Since VfxManager has internal frame buffers,
        // it implements Disposable interface and thus should be utilized properly.
        vfxManager.dispose();

        // *** PLEASE NOTE ***
        // VfxManager doesn't dispose attached VfxEffects.
        // This is your responsibility to manage their lifecycle.
        vfxEffect.dispose();

        shapeRenderer.dispose();
    }
}