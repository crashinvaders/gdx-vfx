package com.crashinvaders.vfx.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;

public class DefaultVfxGlExtension implements VfxGlExtension {
    private static final IntBuffer tmpIntBuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();
    private static final Viewport tmpViewport = new Viewport();

    @Override
    public int getBoundFboHandle() {
        IntBuffer intBuf = tmpIntBuf;
        Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, intBuf);
        return intBuf.get(0);
    }

    @Override
    public Viewport getViewport() {
        IntBuffer intBuf = tmpIntBuf;
        Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf);
        return tmpViewport.set(intBuf.get(0), intBuf.get(1), intBuf.get(2), intBuf.get(3));
    }
}
