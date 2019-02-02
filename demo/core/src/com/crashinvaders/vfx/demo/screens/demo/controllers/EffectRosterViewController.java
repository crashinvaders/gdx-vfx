package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.common.lml.CommonLmlParser;
import com.crashinvaders.common.viewcontroller.LmlViewController;
import com.crashinvaders.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.PostProcessor;
import com.crashinvaders.vfx.PostProcessorEffect;
import com.crashinvaders.vfx.effects.*;
import com.crashinvaders.vfx.filters.RadialBlur;
import com.github.czyzby.lml.parser.LmlParser;

public class EffectRosterViewController extends LmlViewController {

    private final Array<EffectEntryModel> effectsRoster = new Array<>(true, 16);
    private final ArrayMap<EffectEntryModel, EffectEntryViewController> effectsChain = new ArrayMap<>(true, 16);

    private PostProcessor postProcessor;

    private VerticalGroup vgEffectsRoster;
    private VerticalGroup vgEffectsChain;

    public EffectRosterViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser) {
        super(viewControllers, lmlParser);
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        super.onViewCreated(sceneRoot);

        effectsRoster.addAll(
                new EffectEntryModel("Bloom", new BloomEffect()),
                new EffectEntryModel("CRT", new CrtEffect()),
                new EffectEntryModel("CRT (Old TV)", new OldTvEffect()),
                new EffectEntryModel("Chrom. Abber.", new ChromaticAberrationEffect()),
                new EffectEntryModel("Film Grain", new FilmGrainEffect()),
                new EffectEntryModel("Motion Blur", new MotionBlurEffect(Pixmap.Format.RGBA8888, 0.85f)),
                new EffectEntryModel("Radial Blur", new RadialBlurEffect(RadialBlur.Quality.High)),
                new EffectEntryModel("Curvature", new CurvatureEffect()),
                new EffectEntryModel("Lens Flare", new LensFlareEffect()),
                new EffectEntryModel("Lens Flare (Adv)", new LensFlareEffect2(Pixmap.Format.RGBA8888)),
                new EffectEntryModel("Vignette", new VignetteEffect(false)),
                new EffectEntryModel("Zoomer", new ZoomerEffect(1.2f)),
                new EffectEntryModel("FXAA", new FxaaEffect()),
                new EffectEntryModel("NFAA", new NfaaEffect())
        );

        postProcessor = getController(PostProcessorViewController.class).getPostProcessor();

        vgEffectsRoster = sceneRoot.findActor("vgEffectsRoster");
        vgEffectsChain = sceneRoot.findActor("vgEffectsChain");

        for (int i = 0; i < effectsRoster.size; i++) {
            final EffectEntryModel effectModel = effectsRoster.get(i);
            final EffectEntryViewController viewController = new EffectEntryViewController(lmlParser, effectModel);
            final Group viewRoot = viewController.getViewRoot();
            vgEffectsRoster.addActor(viewRoot);

            viewRoot.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    addEffectToChain(effectModel);
                }
            });
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for (int i = 0; i < effectsChain.size; i++) {
            effectsChain.getValueAt(i).update(delta);
        }
    }

    private void addEffectToChain(final EffectEntryModel effectModel) {
        if (effectsChain.containsKey(effectModel)) {
            // If the effect is already in the chain, re-add it to the end of the list.
            removeEffectFromChain(effectModel);
        }

        EffectEntryViewController viewController = new EffectEntryViewController(lmlParser, effectModel);
        Group viewRoot = viewController.getViewRoot();
        vgEffectsChain.addActor(viewRoot);
        effectsChain.put(viewController.getModel(), viewController);
        postProcessor.addEffect(viewController.getModel().getEffect());

        viewRoot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeEffectFromChain(effectModel);
            }
        });
    }

    private void removeEffectFromChain(final EffectEntryModel effectModel) {
        EffectEntryViewController viewController = effectsChain.get(effectModel);
        if (viewController == null) return;

        vgEffectsChain.removeActor(viewController.getViewRoot());
        effectsChain.removeKey(effectModel);
        postProcessor.removeEffect(viewController.getModel().getEffect());
    }

    private static class EffectEntryModel implements Disposable {
        private final String name;
        private final PostProcessorEffect effect;

        public EffectEntryModel(String name, PostProcessorEffect effect) {
            this.name = name;
            this.effect = effect;
        }

        @Override
        public void dispose() {
            effect.dispose();
        }

        public String getName() {
            return name;
        }

        public PostProcessorEffect getEffect() {
            return effect;
        }
    }

    private static class EffectEntryViewController {

        private final EffectEntryModel model;

        private final Stack viewRoot;
        private final Label lblName;

        private final UpdateableEffect updateableEffect;

        public EffectEntryViewController(LmlParser lmlParser, EffectEntryModel model) {
            this.model = model;

            if (model.effect instanceof UpdateableEffect) {
                this.updateableEffect = (UpdateableEffect) model.effect;
            } else {
                this.updateableEffect = null;
            }

            // Create view.
            {
                Skin skin = lmlParser.getData().getDefaultSkin();
                lblName = new Label("", skin);
                lblName.setAlignment(Align.left);
                viewRoot = new Stack(lblName);
                viewRoot.setUserObject(this);
                viewRoot.setTouchable(Touchable.enabled);
            }

            updateViewFromModel();
        }

        public void update(float delta) {
            if (updateableEffect != null) {
                updateableEffect.update(delta);
            }
        }

        public void updateViewFromModel() {
            lblName.setText(model.getName());
        }

        public Group getViewRoot() {
            return viewRoot;
        }

        public EffectEntryModel getModel() {
            return model;
        }
    }
}
