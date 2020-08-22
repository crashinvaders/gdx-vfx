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

package com.crashinvaders.vfx.demo.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.crashinvaders.vfx.demo.App;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("GDX VFX Demo");
		config.setWindowIcon(Files.FileType.Classpath,
				"gdx-vfx-icon16.png",
				"gdx-vfx-icon32.png",
				"gdx-vfx-icon64.png",
				"gdx-vfx-icon128.png");
		config.setWindowedMode(640, 480);
		config.setWindowSizeLimits(32, 32, -1, -1);

		new Lwjgl3Application(new App(), config);
	}
}