package com.crashinvaders.common.gl;

/** The interface lets to customize some extra OpenGL functionality
 * (methods yet not implemented/unsupported by official LibGDX backends).*/
public interface GLExtMethods {
    int getBoundFboHandle();
    Viewport getViewport();

    class Viewport {
        public int x, y, width, height;

        public Viewport set(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public Viewport set(Viewport viewport) {
            this.x = viewport.x;
            this.y = viewport.y;
            this.width = viewport.width;
            this.height = viewport.height;
            return this;
        }

        @Override
        public String toString() {
            return "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height;
        }
    }
}
