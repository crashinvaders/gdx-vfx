![Logo](https://i.imgur.com/kVBGQHx.png)

[![Maven Central](https://img.shields.io/maven-central/v/com.crashinvaders.vfx/gdx-vfx-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.crashinvaders.vfx%22%20AND%20a:%22gdx-vfx-core%22)
[![libGDX](https://img.shields.io/badge/libgdx-1.12.0-red.svg)](https://libgdx.com/)

Flexible post-processing shader visual effects for LibGDX. The library is based on [libgdx-contribs-postprocessing](https://github.com/manuelbua/libgdx-contribs/tree/master/postprocessing), with lots of improvements and heavy refactoring.
The goal is to focus on stability, offer lightweight integration and provide simple effect implementation mechanism.

The library is in Beta, the code is poorly documented. Some goodies might be missing and more cool stuff is to be implemented soon.

Read more about the library at the [wiki introduction page](https://github.com/crashinvaders/gdx-vfx/wiki/Library-overview).

All the major changes are listed in the [CHANGES.md](https://github.com/crashinvaders/gdx-vfx/blob/master/CHANGES.md) file.

# Known problems in 0.5.1
- iOS integration requires an extra step in order to make the `gdx-vfx-effects` asset files available on runtime. Please read [this thread](https://github.com/crashinvaders/gdx-vfx/issues/16#issuecomment-1003156513) for temporary workaround.

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
    implementation 'com.crashinvaders.vfx:gdx-vfx-core:0.5.4'
    implementation 'com.crashinvaders.vfx:gdx-vfx-effects:0.5.4'    // Optional, if you need standard filter/effects.
}
```

#### HTML/GWT support
The library is fully HTML/GWT compatible, but requires an extra dependency to be included to GWT module in order to work properly.  
Please consider reading [GWT integration guide](https://github.com/crashinvaders/gdx-vfx/wiki/GWT-HTML-Library-Integration).
```gradle
dependencies {
    implementation 'com.crashinvaders.vfx:gdx-vfx-gwt:0.5.4'
}
```

#### Other integration options
There are number of ways to incorporate the library into the project. 
If you're looking for snapshot version artifacts or another approach, please read the [general integration guide](https://github.com/crashinvaders/gdx-vfx/wiki/General-Library-Integration).

### 2. Sample code

A simple example of a LibGDX application that applies gaussian blur effect to a geometry drawn with `ShapeRenderer`.

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
        // They provide public properties to configure and control them.
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
        vfxManager.beginInputCapture();

        // Here's where game render should happen.
        // For demonstration purposes we just render some simple geometry.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.PINK);
        shapeRenderer.rect(250f, 100f, 250f, 175f);
        shapeRenderer.setColor(Color.ORANGE);
        shapeRenderer.circle(200f, 250f, 100f);
        shapeRenderer.end();

        // End render to an off-screen buffer.
        vfxManager.endInputCapture();

        // Apply the effects chain to the captured frame.
        // In our case, only one effect (gaussian blur) will be applied.
        vfxManager.applyEffects();

        // Render result to the screen.
        vfxManager.renderToScreen();
    }

    @Override
    public void dispose() {
        // Since VfxManager has internal frame buffers,
        // it implements Disposable interface and thus should be utilized properly.
        vfxManager.dispose();

        // *** PLEASE NOTE ***
        // VfxManager doesn't dispose attached VfxEffects.
        // This is your responsibility to manage their lifecycle.
        vfxEffect.dispose();

        shapeRenderer.dispose();
    }
}
``` 

![Result](https://i.imgur.com/XjBynGw.png)

_The actual example code can be found [here](https://github.com/crashinvaders/gdx-vfx/blob/master/demo/core/src/com/crashinvaders/vfx/demo/screens/example/VfxExample.java)._
