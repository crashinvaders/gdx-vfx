package com.crashinvaders.vfx.demo;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.crashinvaders.vfx.common.PrioritizedInputMultiplexer;
import com.crashinvaders.vfx.demo.screens.demo.DemoScreen;

public class App extends Game {

	private static App instance;

//	static {
//		ShaderLoader.basePath = "shaders/";
//		ShaderLoader.pedantic = true;
//	}

	private final PrioritizedInputMultiplexer inputMultiplexer;

	private DemoScreen demoScreen;

	public static App inst() {
		if (instance == null) {
			throw new NullPointerException("App is not initialized yet!");
		}
		return instance;
	}

	public App() {
		inputMultiplexer = new PrioritizedInputMultiplexer();
		inputMultiplexer.setMaxPointers(Integer.MAX_VALUE);
		inputMultiplexer.addProcessor(new GlobalInputHandler(), -Integer.MAX_VALUE);
	}

	@Override
	public void create () {
		instance = this;

		Gdx.input.setInputProcessor(inputMultiplexer);

		demoScreen = new DemoScreen();
		setScreen(demoScreen);
	}
	
	@Override
	public void dispose () {
		super.dispose();
		demoScreen.dispose();
	}

	//region Accessors
	public PrioritizedInputMultiplexer getInput() {
		return inputMultiplexer;
	}
	//endregion

	private void restartApp() {
		dispose();
		create();
	}

	private class GlobalInputHandler extends InputAdapter {
		@Override
		public boolean keyDown(int keycode) {
			switch (keycode) {
				case Input.Keys.F8:
					// Restart entire game.
					restartApp();
					return true;
				default:
					return super.keyDown(keycode);
			}
		}
	}
}
