package com.crashinvaders.vfx.demo.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.crashinvaders.vfx.demo.App;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "GDX VFX Demo";
		config.addIcon("gdx-vfx-icon16.png", Files.FileType.Classpath);
		config.addIcon("gdx-vfx-icon32.png", Files.FileType.Classpath);
		config.addIcon("gdx-vfx-icon64.png", Files.FileType.Classpath);
		config.addIcon("gdx-vfx-icon128.png", Files.FileType.Classpath);
		config.width = 640;
		config.height = 480;
		new LwjglApplication(new App(), config);
	}
}