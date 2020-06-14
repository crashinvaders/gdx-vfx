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

package com.crashinvaders.vfx.gl;

import com.badlogic.gdx.Gdx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;

public class DefaultVfxGlExtension implements VfxGlExtension {
    private static final IntBuffer tmpIntBuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();

    @Override
    public int getBoundFboHandle() {
        IntBuffer intBuf = tmpIntBuf;
        Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, intBuf);
        return intBuf.get(0);
    }
}
