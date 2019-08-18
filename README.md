![](https://i.imgur.com/Z512PcQ.png)

# 

[![Build Status](https://travis-ci.org/crashinvaders/gdx-vfx.svg?branch=master)](https://travis-ci.org/crashinvaders/gdx-vfx)
[![](https://jitpack.io/v/crashinvaders/gdx-vfx.svg)](https://jitpack.io/#crashinvaders/gdx-vfx)

LibGDX flexible post processing visual effects. The library is based on [libgdx-contribs-postprocessing](https://github.com/manuelbua/libgdx-contribs/tree/master/postprocessing), 
with lots of improvements, aim on stability and to provide lightweight integration with comfortable effect extensions.

Work is in progress, some official backends are not stable yet.
More effects to be implemented and included in the standard library package.

# Demo

Visit https://crashinvaders.github.io/gdx-vfx

Or clone and play with the demo locally:
```
git clone https://github.com/crashinvaders/gdx-vfx.git
cd gdx-vfx
./gradlew demo:desktop:run
```

![Alt Text](https://imgur.com/dCsVhoo.gif)


# How to use

### 1. Add the library to the project

#### Maven dependency
The library currently is in beta, thus it's not available as a public release on Maven Central. But with help of [JitPack](https://jitpack.io/#crashinvaders/gdx-vfx) we still can reference the library as a maven dependency.

Add it in your root build.gradle at the end of repositories:
```gradle
allprojects {
	repositories {
		// ...
		maven { url 'https://jitpack.io' }
	}
}
```

Add the dependency:
```gradle
dependencies {
	// ...
    	implementation 'com.github.crashinvaders.gdx-vfx:core:0.2'
    	implementation 'com.github.crashinvaders.gdx-vfx:effects:0.2'
}
```

#### Other options
There are number of ways to incorporate the library into the project. If you're looking for the other appoach, please read the full [integration guide in wiki](https://github.com/crashinvaders/gdx-vfx/wiki/Library-integration).

#### HTML/GWT support
If your project has a GWT module, please consider reading [this wiki page](https://github.com/crashinvaders/gdx-vfx/wiki/GWT-HTML-Integration).

### 2. Sample code

```java
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.crashinvaders.vfx.PostProcessor;
import com.crashinvaders.vfx.effects.BloomEffect;

public class PostProcessorExample extends ApplicationAdapter {

	private ShapeRenderer shapeRenderer;
	private PostProcessor postProcessor;
	private BloomEffect postProcessorEffect;

	@Override
	public void create() {
		shapeRenderer = new ShapeRenderer();

		// PostProcessor is a manager for the effects.
		// It captures rendering into internal off-screen buffer and applies a chain of defined effects.
		// Off-screen buffers may have any pixel format, but for better effect mixing
		// it's recommended to use values with an alpha component (e.g. RGBA8888 or RGBA4444).
		postProcessor = new PostProcessor(Pixmap.Format.RGBA8888);

		// PostProcessor must be initialized with the required size for internal off-screen buffers.
		postProcessor.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Create and add a shader effect.
		// PostProcessorEffect derivative classes serves as controllers for shader effects.
		// They usually provide some public properties to configure and control the effects.
		postProcessorEffect = new BloomEffect();
		postProcessorEffect.setBlurPasses(32);
		postProcessorEffect.setBloomIntesity(1.2f);
		postProcessor.addEffect(postProcessorEffect);
	}

	@Override
	public void resize(int width, int height) {
		// PostProcessor manages internal off-screen buffers,
		// which should always match the required viewport (whole screen in our case).
		postProcessor.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Don't forget to update batch projection matrix as screen viewport has changed.
        shapeRenderer.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
        shapeRenderer.updateMatrices();
	}

	@Override
	public void render() {
		// Clean up the screen.
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Begin render to an off-screen buffer.
		postProcessor.beginCapture();

		// Render some simple geometry.
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.PINK);
		shapeRenderer.rect(250f, 100f, 250f, 175f);
		shapeRenderer.setColor(Color.ORANGE);
		shapeRenderer.circle(200f, 250f, 100f);
		shapeRenderer.end();

		// End render to an off-screen buffer.
		postProcessor.endCapture();

		// Perform shader processing and render result to the screen buffer.
		postProcessor.render();
	}
	
	@Override
	public void dispose() {
		// Since PostProcessor manages internal off-screen buffers,
		// it should be disposed properly.
		postProcessor.dispose();

		// *** PLEASE NOTE ***
		// PostProcessor doesn't dispose attached PostProcessorEffects
		// on its own, you should do it manually!
		postProcessorEffect.dispose();

		shapeRenderer.dispose();
	}
}
```

![Result](https://i.imgur.com/qSaIEWD.png)
