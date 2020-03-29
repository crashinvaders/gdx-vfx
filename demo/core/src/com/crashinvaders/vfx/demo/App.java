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

package com.crashinvaders.vfx.demo;

import com.badlogic.gdx.*;
import com.crashinvaders.vfx.common.PrioritizedInputMultiplexer;
import com.crashinvaders.vfx.demo.screens.demo.DemoScreen;
import com.crashinvaders.vfx.demo.screens.example.ExampleScreen;

public class App extends Game {

	private static App instance;

	static {
//		ShaderProgram.pedantic = false;
	}

	private final PrioritizedInputMultiplexer inputMultiplexer;

	private Screen mainScreen;

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

		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.input.setInputProcessor(inputMultiplexer);

		mainScreen = new DemoScreen();
//		mainScreen = new ExampleScreen();
		setScreen(mainScreen);
	}
	
	@Override
	public void dispose () {
		super.dispose();
		mainScreen.dispose();
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
					// Restart the app.
					restartApp();
					return true;
				default:
					return super.keyDown(keycode);
			}
		}
	}
}
