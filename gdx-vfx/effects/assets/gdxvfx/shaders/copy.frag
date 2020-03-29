#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

varying vec2 v_texCoords;

uniform sampler2D u_texture0;

void main() {
	gl_FragColor = texture2D(u_texture0, v_texCoords);
}