// Simple motion blur implementation by Toni Sagrista
// Last frame is drawn with lower opacity

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

#ifndef METHOD
    #error Please define METHOD
#endif

#define MAX 0
#define MIX 1

uniform PRECISION sampler2D u_texture0;
uniform PRECISION sampler2D u_texture1;
uniform float u_mix;

varying vec2 v_texCoords;

void main() {
#if METHOD == MAX
    gl_FragColor = max(
        texture2D(u_texture0, v_texCoords),
        texture2D(u_texture1, v_texCoords) * u_mix);
#elif METHOD == MIX
    gl_FragColor = mix(
        texture2D(u_texture0, v_texCoords),
        texture2D(u_texture1, v_texCoords),
        u_mix);
#else
    #error Unexpected METHOD value
#endif
}
