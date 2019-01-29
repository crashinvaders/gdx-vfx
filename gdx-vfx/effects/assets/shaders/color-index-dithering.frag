#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform sampler2D u_palette;
uniform int u_viewportWidth;
uniform int u_viewportHeight;
varying vec2 v_texCoords;

void main(void) {
	vec4 index = texture2D(u_texture0, v_texCoords);
    vec3 color = texture2D(u_palette, index.rg * vec2(index.a, index.a)).rgb;
    float alpha = 1.0;
    if (index.a < 0.05) {
        alpha = 0.0;
    } else if (index.a < 0.15) {
        float x = ceil(float(u_viewportWidth) * v_texCoords.x);
        float y = ceil(float(u_viewportHeight) * v_texCoords.y);
        if (mod(x, 2.0) < 0.5 || mod(y, 2.0) < 0.5) {
            alpha = 0.0;
        }
    } else if (index.a < 0.30) {
        float x = ceil(float(u_viewportWidth) * v_texCoords.x);
        float y = ceil(float(u_viewportHeight) * v_texCoords.y);
        if (mod(x+y, 2.0) > 0.5) {
            alpha = 0.0;
        }
    }
    gl_FragColor = vec4(color, alpha);
}