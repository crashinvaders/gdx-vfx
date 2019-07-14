#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform PRECISION sampler2D u_texture0;
uniform PRECISION sampler2D u_texture1;
uniform float u_mix;

varying vec2 v_texCoords;

void main() {
	gl_FragColor = mix(
		texture2D(u_texture0, v_texCoords),
		texture2D(u_texture1, v_texCoords),
		u_mix);
}