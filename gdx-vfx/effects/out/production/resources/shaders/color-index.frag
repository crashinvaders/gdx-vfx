#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform sampler2D u_palette;
varying vec2 v_texCoords;

void main(void) {
	vec2 index = texture2D(u_texture0, v_texCoords).rg;
    vec3 color = texture2D(u_palette, index.rg).rgb;
    gl_FragColor = vec4(color, 1.0);
}