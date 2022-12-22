## [0.5.1]

- LibGDX version is updated to 1.11.0

- `VfxEffect` constrains its internal width and height on `#resize(int, int)`.
Max side size of the internal buffers is now limited to the `VfxEffect#MAX_FRAME_BUFFER_SIDE` constant value which is 8192 by default.
Feel free to change it according to you project requirements.

## [0.5.0]

- GWT module launcher no longer should make a call to `GwtVfxGlExtension#initialize()`. 
Please revisit GWT integration guide on the wiki page.

- `VfxManager#render()` method was broken down into separate stages (methods):

    1. `VfxManager#applyEffects()` applies the effect chain to the captured result.
    
    2. `VfxManager#renderToScreen()`/`VfxManager#renderToFbo()` methods are now responsible for rendering the result. 
    You also have an option to retrieve the current result using `VfxManager#getResultBuffer()` and perform custom drawing (e.g. render buffer's texture using `SpriteBatch`).
    
- `VfxManager`'s `#beginCapture()`/`#endCapture()` have been renamed to `#beginInputCapture()`/`#endInputCapture()`.

- As an alternative to `#beginSceneCapture()`/`#endSceneCapture()`, 
an input texture/buffer may be supplied through `VfxManager#useAsInput()` methods.

- `VfxManager` no longer cleans up the buffers when begin input capture. 
You should make an explicit call to `VfxManager#cleanUpBuffers()` 
in order to reset previous result before start capturing/providing a new frame.

- Filters/effects cleanup. Some getters/setters were renamed.

- `VfxEffect` has got `#update(float)` method to update time based values. You should call `VfxManager#update(float)` every frame in order to update the effects.

- `VfxEffect` no longer has any `render()` related methods, it's now just a base interface that's declares the most essential lifecycle methods (like `resize()`, `rebind()`, `dispose()`, etc).

- The concept of _Filters_ was eliminated, there are only _Effects_ now. Although not every effect is made for `VfxManager` chaining render (read below).

- Any effect that wants to participate in `VfxManager`'s effects chain, should implement `ChainVfxEffect` interface. A good example of non-chain effect is `CopyEffect` or `MixEffect`, they are more like utility effect units, provide specific render capabilities and can take part in complex effect pipelines (e.g. `CompositeVfxEffect`).   

- `NoiseEffect` was removed due to similarities with `FilmGrainEffect`.

- `PingPongBuffer` replaced with `VfxPingPongWrapper` which is a lightweight version and doesn't manage its own pair of `VfxFrameBuffer`, but instead works only with the provided instances.
`VfxPingPongWrapper` can be integrated with `VfxFrameBufferPool`.

- `VfxFrameBufferPool` added. It acts like a regular LibGDX `Pool`, but with a twist... It manages the lifecycle of all the created `VfxFrameBuffer` instances internally.

- `VfxWidgetGroup` is now fully compatible with transform enabled parent/child actors (check out web demo).

- `VfxWidgetGroup`'s internal frame buffer may be configured to resize to widget's size or to match real screen PPU.

- `IntegerRoundFillContainer` was removed as `VfxWidgetGroup` supersets its functionality.
