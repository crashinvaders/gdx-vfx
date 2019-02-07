package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.crashinvaders.common.lml.CommonLmlParser;
import com.crashinvaders.common.viewcontroller.LmlViewController;
import com.crashinvaders.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.PostProcessor;
import com.crashinvaders.vfx.scene2d.IntegerRoundFillContainer;
import com.crashinvaders.vfx.scene2d.PostProcessorWidgetGroup;
import com.github.czyzby.lml.annotation.LmlAction;

public class PostProcessorViewController extends LmlViewController {

    private final Color clearColor;

    private PostProcessor postProcessor;
    private WidgetGroup canvasRoot;

    public PostProcessorViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser, Color clearColor) {
        super(viewControllers, lmlParser);
        this.clearColor = clearColor;
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        super.onViewCreated(sceneRoot);

    }

    @Override
    public void dispose() {
        super.dispose();
        postProcessor.dispose();
    }

    public PostProcessor getPostProcessor() {
        return postProcessor;
    }

    public Group getCanvasRoot() {
        return canvasRoot;
    }

    @LmlAction Actor createCanvas() {
        canvasRoot = new WidgetGroup();
        canvasRoot.setName("canvasRoot");
        canvasRoot.setFillParent(true);

        PostProcessorWidgetGroup postProcessingGroup = new PostProcessorWidgetGroup(Pixmap.Format.RGBA8888);
        postProcessor = postProcessingGroup.getPostProcessor();
        postProcessor.setBlendingEnabled(false);
        postProcessor.setCleanUpBuffers(true);
        postProcessor.setClearColor(clearColor);
        postProcessingGroup.addActor(canvasRoot);

        IntegerRoundFillContainer postProcessorContainer = new IntegerRoundFillContainer(postProcessingGroup);
        return postProcessorContainer;
    }
}
