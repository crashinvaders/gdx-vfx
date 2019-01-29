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

float minDist = 0.25;

void main() {
	vec4 pointC = texture2D(u_texture0, v_texCoords);
	float dist = distance(v_texCoords, vec2(0.5,0.5));
	float rate = 0.0;
	if (dist > minDist) {
	    rate = 1.5 * (dist - minDist) * u_intensity;
    }
	gl_FragColor = pointC * (1.0 - rate) + vec4(u_color, 1.0) * rate;
}