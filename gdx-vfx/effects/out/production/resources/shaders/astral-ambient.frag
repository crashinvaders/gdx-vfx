#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
varying vec2 v_texCoords;

uniform float u_intensity;

void main() {
	vec4 pointC = texture2D(u_texture0, v_texCoords);
	float ui = u_intensity;
	pointC.b *= 1.0 + u_intensity * 0.4;
	pointC.g *= 1.0 - u_intensity * 0.6;
	gl_FragColor = pointC;
}