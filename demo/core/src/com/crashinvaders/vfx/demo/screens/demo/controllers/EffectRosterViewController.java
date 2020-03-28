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

package com.crashinvaders.vfx.demo.screens.demo.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.common.lml.CommonLmlParser;
import com.crashinvaders.vfx.common.lml.LmlUtils;
import com.crashinvaders.vfx.common.viewcontroller.LmlViewController;
import com.crashinvaders.vfx.common.viewcontroller.ViewControllerManager;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.*;
import com.crashinvaders.vfx.effects.util.CopyEffect;
import com.crashinvaders.vfx.effects.util.MixEffect;
import com.github.czyzby.lml.annotation.LmlActor;
import com.github.czyzby.lml.parser.LmlParser;

public class EffectRosterViewController extends LmlViewController {

    private final Array<EffectEntryModel> effectsRoster = new Array<>(true, 16);
    private final ArrayMap<EffectEntryModel, EffectEntryViewController> effectsChain = new ArrayMap<>(true, 16);

    private VfxManager vfxManager;

    private VerticalGroup vgEffectsRoster;
    private VerticalGroup vgEffectsChain;

    public EffectRosterViewController(ViewControllerManager viewControllers, CommonLmlParser lmlParser) {
        super(viewControllers, lmlParser);
    }

    @Override
    public void onViewCreated(Group sceneRoot) {
        super.onViewCreated(sceneRoot);

        // Bloom
        {
            BloomEffect filter = new BloomEffect();
            effectsRoster.add(new EffectEntryModel("Bloom", filter));
        }
        // Copy
        {
            CopyEffect filter = new CopyEffect();
            effectsRoster.add(new EffectEntryModel("Copy", filter));
        }
        // Radial Distortion
        {
            RadialDistortionEffect filter = new RadialDistortionEffect();
            filter.setZoom(0.9f);
            filter.setDistortion(0.3f);
            effectsRoster.add(new EffectEntryModel("Radial Distortion", filter));
        }
        // Gamma Threshold
        {
            GammaThresholdEffect filter = new GammaThresholdEffect(GammaThresholdEffect.Type.RGB);
            filter.setGamma(0.9f);
            effectsRoster.add(new EffectEntryModel("Gamma Threshold", filter));
        }
        // Zoom
        {
            ZoomEffect filter = new ZoomEffect();
            filter.setZoom(0.9f);
            effectsRoster.add(new EffectEntryModel("Zoom", filter));
        }
        // Vignetting
        {
            VignettingEffect filter = new VignettingEffect(false);
            effectsRoster.add(new EffectEntryModel("Vignetting", filter));
        }
        // CRT
        {
            CrtEffect filter = new CrtEffect(CrtEffect.LineStyle.HORIZONTAL_SMOOTH, 1.3f, 0.8f);
            filter.setSizeSource(CrtEffect.SizeSource.VIEWPORT);
            effectsRoster.add(new EffectEntryModel("CRT", filter));
        }
        // FXAA
        {
            FxaaEffect filter = new FxaaEffect();
            effectsRoster.add(new EffectEntryModel("FXAA", filter));
        }
        // NFAA
        {
            NfaaEffect filter = new NfaaEffect(false);
            effectsRoster.add(new EffectEntryModel("NFAA", filter));
        }
        // Lens Flare
        {
            LensFlareEffect filter = new LensFlareEffect();
            effectsRoster.add(new EffectEntryModel("Lens Flare", filter));
        }
        // Fisheye
        {
            FisheyeEffect filter = new FisheyeEffect();
            effectsRoster.add(new EffectEntryModel("Fisheye", filter));
        }
        // Film Grain
        {
            FilmGrainEffect filter = new FilmGrainEffect();
            filter.setNoiseAmount(0.18f);
            effectsRoster.add(new EffectEntryModel("Film Grain", filter));
        }
        // Motion Blur (MIX)
        {
            MotionBlurEffect filter = new MotionBlurEffect(Pixmap.Format.RGBA8888, MixEffect.Method.MIX, 0.75f);
            effectsRoster.add(new EffectEntryModel("Motion Blur (MIX)", filter));
        }
        // Motion Blur (MAX)
        {
            MotionBlurEffect filter = new MotionBlurEffect(Pixmap.Format.RGBA8888, MixEffect.Method.MAX, 0.75f);
            effectsRoster.add(new EffectEntryModel("Motion Blur (MAX)", filter));
        }
        // Old TV
        {
            OldTvEffect filter = new OldTvEffect();
            effectsRoster.add(new EffectEntryModel("Old TV", filter));
        }
        // Levels
        {
            LevelsEffect filter = new LevelsEffect();
            filter.setHue(0.95f);
            effectsRoster.add(new EffectEntryModel("Levels", filter));
        }
        // Chrom. Aberration
        {
            ChromaticAberrationEffect filter = new ChromaticAberrationEffect(12);
            effectsRoster.add(new EffectEntryModel("Chrom. Aberration", filter));
        }
        // Radial Blur
        {
            RadialBlurEffect filter = new RadialBlurEffect(8);
            effectsRoster.add(new EffectEntryModel("Radial Blur", filter));
        }
        // Gaussian Blur
        {
            GaussianBlurEffect filter = new GaussianBlurEffect(GaussianBlurEffect.BlurType.Gaussian5x5);
            filter.setPasses(8);
            effectsRoster.add(new EffectEntryModel("Gaussian Blur", filter));
        }


//        effectsRoster.addAll(
//                new EffectEntryModel("Bloom", new BloomEffect(Pixmap.Format.RGBA8888)),
//                new EffectEntryModel("CRT", new CrtEffect(CrtFilterOld.LineStyle.HORIZONTAL_SMOOTH, 1.3f, 0.8f)
//                        .setSizeSource(CrtFilterOld.SizeSource.VIEWPORT)),
//                new EffectEntryModel("Old TV", new OldTvEffect()),
//                new EffectEntryModel("Noise", new NoiseEffect(0.35f, 2f)),
//                new EffectEntryModel("Chrom. Abber.", new ChromaticAberrationEffect()),
//                new EffectEntryModel("Film Grain", new FilmGrainEffect()),
//                new EffectEntryModel("Gaussian Blur", new GaussianBlurEffect(8, GaussianBlurFilter.BlurType.Gaussian5x5)),
//                new EffectEntryModel("Motion Blur (MAX)", new MotionBlurEffect(Pixmap.Format.RGBA8888, MotionBlurFilterOld.BlurFunction.MAX, 0.75f)),
//                new EffectEntryModel("Motion Blur (MIX)", new MotionBlurEffect(Pixmap.Format.RGBA8888, MotionBlurFilterOld.BlurFunction.MIX, 0.75f)),
//                new EffectEntryModel("Radial Blur", new RadialBlurEffect(8)),
//                new EffectEntryModel("Curvature", new CurvatureEffect()),
//                new EffectEntryModel("Lens Flare", new LensFlareEffect()
//                        .setIntensity(10f)),
//                new EffectEntryModel("Vignette", new VignetteEffect(false)),
//                new EffectEntryModel("Zoomer", new ZoomerEffect(1.2f)),
//                new EffectEntryModel("FXAA", new FxaaEffect()),
//                new EffectEntryModel("NFAA", new NfaaEffect()),
//                new EffectEntryModel("Fisheye", new FisheyeEffect()),
//                new EffectEntryModel("HDR", new HdrEffect(3.0f, 2.2f)),
//                new EffectEntryModel("Levels", new LevelsEffect()
//                        .setBrightness(0.1f)
//                        .setSaturation(1.8f)
//                        .setContrast(1.5f)
//                        .setHue(0.9f)
//                        .setGamma(1.0f))
//        );

        vfxManager = getController(VfxViewController.class).getVfxManager();

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

    private void addEffectToChain(final EffectEntryModel effectModel) {
        if (effectsChain.containsKey(effectModel)) {
            // If the effect is already in the chain, re-add it to the end of the list.
            removeEffectFromChain(effectModel);
        }

        EffectEntryViewController viewController = new EffectEntryViewController(lmlParser, effectModel);
        Group viewRoot = viewController.getViewRoot();
        vgEffectsChain.addActor(viewRoot);
        effectsChain.put(viewController.getModel(), viewController);
        vfxManager.addEffect(viewController.getModel().getEffect());

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
        vfxManager.removeEffect(viewController.getModel().getEffect());
    }

    private static class EffectEntryModel implements Disposable {
        private final String name;
        private final ChainVfxEffect effect;

        public EffectEntryModel(String name, ChainVfxEffect effect) {
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

        public ChainVfxEffect getEffect() {
            return effect;
        }
    }

    public static class EffectEntryViewController {

        @LmlActor Label lblName;

        private final EffectEntryModel model;
        private final Group viewRoot;

        EffectEntryViewController(LmlParser lmlParser, EffectEntryModel model) {
            this.model = model;

            // Create view.
            viewRoot = LmlUtils.parseLmlTemplate(lmlParser, this, Gdx.files.internal("lml/screen-demo/effect-list-item.lml"));
            viewRoot.setUserObject(this);

            updateViewFromModel();
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
