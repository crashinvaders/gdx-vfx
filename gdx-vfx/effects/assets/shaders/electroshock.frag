#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
varying vec2 v_texCoords;

uniform float u_bleachIntensity;
uniform float u_imprintIntensity;

void main() {
	vec4 pointC = texture2D(u_texture0, v_texCoords);
	vec4 overlayPointC = texture2D(u_texture1, v_texCoords);
	vec4 mixColor = mix(pointC, overlayPointC, u_imprintIntensity);
	gl_FragColor = mixColor + u_bleachIntensity;
}