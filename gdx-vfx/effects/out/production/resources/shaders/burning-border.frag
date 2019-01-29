#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
varying vec2 v_texCoords;

uniform vec3 u_color;
uniform float u_intensity;

float calcRate(float pos) {
	float centerY = 1.0;
	if (pos > 0.5) {
		centerY = 0.0;
	}
	float rate = distance(vec2(0.5, centerY), v_texCoords);
	rate = smoothstep(0.6, 1.5, rate);
	return rate;
}

void main() {
	float rate = calcRate(v_texCoords.y) * u_intensity;
	vec4 pointC = texture2D(u_texture0, v_texCoords);
	gl_FragColor = pointC * (1.0 - rate) + vec4(u_color, 1.0) * rate;
}