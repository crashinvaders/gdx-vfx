package com.crashinvaders.common.gl;

public class GLUtils {
    //TODO Remove this after https://github.com/libgdx/libgdx/issues/4688 gets resolved
    // This field may be used to provide custom implementation
    public static GLExtCalls customCalls = new DefaultGLExtCalls();

    public static int getBoundFboHandle() {
        return customCalls.getBoundFboHandle();
    }

    public static GLExtCalls.Viewport getViewport() {
        return customCalls.getViewport();
    }
}
