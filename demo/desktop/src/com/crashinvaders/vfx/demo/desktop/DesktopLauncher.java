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

		new Lwjgl3Application(new App(), config);
	}
}