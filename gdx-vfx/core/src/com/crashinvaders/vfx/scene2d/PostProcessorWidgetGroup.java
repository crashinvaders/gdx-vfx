package com.crashinvaders.vfx.scene2d;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.PostProcessorManager;

public class PostProcessorWidgetGroup extends WidgetGroup {

    private final PostProcessorManager postProcessorManager;
    private boolean initialized = false;
    private boolean resizePending = false;

    public PostProcessorWidgetGroup(Pixmap.Format pixelFormat) {
        postProcessorManager = new PostProcessorManager(pixelFormat);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);

        if (stage != null && !initialized) {
            initialized = true;
            initialize();
        }
        if (stage == null && initialized) {
            initialized = false;
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
            postProcessorManager.resize(
                    MathUtils.round(getWidth()),
                    MathUtils.round(getHeight()));
        }
        postProcessorManager.beginCapture();
        batch.begin();
        super.draw(batch, parentAlpha);
        batch.end();
        postProcessorManager.endCapture();
        postProcessorManager.render();

        batch.begin();
    }

    public PostProcessorManager getPostProcessorManager() {
        return postProcessorManager;
    }

    private void initialize() {
        int width = (int)getWidth();
        int height = (int)getHeight();
        if (width == 0 || height == 0) {
            Viewport viewport = getStage().getViewport();
            width = MathUtils.round(viewport.getWorldWidth());
            height = MathUtils.round(viewport.getWorldHeight());
        }
        postProcessorManager.resize(width, height);
        resizePending = false;
    }

    private void reset() {
        resizePending = false;
        postProcessorManager.dispose();
    }
}
