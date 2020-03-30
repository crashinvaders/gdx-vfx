![Logo](https://i.imgur.com/kVBGQHx.png)

[![Maven Central](https://img.shields.io/maven-central/v/com.crashinvaders.vfx/gdx-vfx-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.crashinvaders.vfx%22%20AND%20a:%22gdx-vfx-core%22)
[![Build Status](https://travis-ci.org/crashinvaders/gdx-vfx.svg?branch=master)](https://travis-ci.org/crashinvaders/gdx-vfx)

Flexible post-processing shader visual effects for LibGDX. The library is based on [libgdx-contribs-postprocessing](https://github.com/manuelbua/libgdx-contribs/tree/master/postprocessing), with lots of improvements and heavy refactoring.
The goal is to focus on stability, offer lightweight integration and provide simple effect implementation mechanism.

The library is in Beta, code is poorly documented. Some goodies might be missing and more cool stuff is to be implemented soon.

Read more about the library at the [wiki introduction page](https://github.com/crashinvaders/gdx-vfx/wiki/Library-overview).

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
The library's stable releases are available through maven central repo.

Add it in your root `build.gradle` at the end of repositories:
```gradle
allprojects {
    repositories {
        mavenCentral()
    }
}
```

Add the dependency:
```gradle
dependencies {
    implementation 'com.crashinvaders.vfx:gdx-vfx-core:0.4.3'
    implementation 'com.crashinvaders.vfx:gdx-vfx-effects:0.4.3'    // Optional, if you need standard filter/effects.
}
```

#### HTML/GWT support
If your project has a GWT module, please consider reading [this wiki page](https://github.com/crashinvaders/gdx-vfx/wiki/GWT-HTML-Library-Integration).

#### Other options
There are number of ways to incorporate the library into the project. If you're looking for the other appoach, please read the full [integration guide](https://github.com/crashinvaders/gdx-vfx/wiki/General-Library-Integration).

### 2. Sample code

```java
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;

public class VfxExample extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private VfxManager vfxManager;
    private GaussianBlurEffect vfxEffect;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();

        // VfxManager is a host for the effects.
        // It captures rendering into internal off-screen buffer and applies a chain of defined effects.
        // Off-screen buffers may have any pixel format, for this example we will use RGBA8888.
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);

        // Create and add an effect.
        // VfxEffect derivative classes serve as controllers for the effects.
        // and provide some public properties to configure and control them.
        vfxEffect = new GaussianBlurEffect();
        vfxManager.addEffect(vfxEffect);
    }

    @Override
    public void resize(int width, int height) {
        // VfxManager manages internal off-screen buffers,
        // which should always match the required viewport (whole screen in our case).
        vfxManager.resize(width, height);

        shapeRenderer.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
        shapeRenderer.updateMatrices();
    }

    @Override
    public void render() {
        // Clean up the screen.
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Clean up internal buffers, as we don't need any information from the last render.
        vfxManager.cleanUpBuffers();

        // Begin render to an off-screen buffer.
        vfxManager.beginCapture();

        // Here's where game render should happen.
        // For demonstration purposes we just render some simple geometry.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(250f, 100f, 250f, 175f);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(200f, 250f, 100f);
        shapeRenderer.end();

        // End render to an off-screen buffer.
        vfxManager.endCapture();

        // Apply the effects chain to the captured frame.
        // In our case the only one effect (gaussian blur) will be applied.
        vfxManager.applyEffects();

        // Render result to the screen.
        vfxManager.renderToScreen();
    }

    @Override
    public void dispose() {
        // Since VfxManager manages internal off-screen buffers,
        // it should be disposed properly.
        vfxManager.dispose();

        // *** PLEASE NOTE ***
        // VfxManager doesn't dispose attached VfxEffects
        // on its own, you should do it manually!
        vfxEffect.dispose();

        shapeRenderer.dispose();
    }
}
```

![Result](https://i.imgur.com/XjBynGw.png)
