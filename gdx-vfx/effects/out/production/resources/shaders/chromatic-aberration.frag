// Originally based on
// https://github.com/spite/Wagner/blob/master/fragment-shaders/chromatic-aberration-fs.glsl

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

const float MAX_DISTORT = 1.2;
const int NUM_ITER = 12;
const float RECI_NUM_ITER_F = 1.0 / float(NUM_ITER);

uniform sampler2D u_texture0;
//uniform vec2 u_resolution;

varying vec2 v_texCoords;

vec2 barrelDistortion(vec2 coord, float amt) {
	vec2 cc = coord - 0.5;
	float dist = dot(cc, cc);
	return coord + cc * dist * amt;
}

float sat(float t) {
	return clamp(t, 0.0, 1.0);
}

float linterp(float t) {
	return sat(1.0 - abs(2.0 * t - 1.0));
}


float remap(float t, float a, float b) {
	return sat((t - a) / (b - a));
}

vec4 spectrumOffset(float t) {
	vec4 ret;
	float lo = step(t, 0.5);
	float hi = 1.0 - lo;
	float w = linterp(remap(t, 1.0 / 6.0, 5.0 / 6.0));
	ret = vec4(lo, 1.0, hi, 1.0) * vec4(1.0 - w, w, 1.0 - w, 1.0);

	return pow(ret, vec4(1.0 / MAX_DISTORT));
}
void main() {
	vec2 uv = v_texCoords;

	vec4 sumcol = vec4(0.0);
	vec4 sumw = vec4(0.0);
	for (int i = 0; i < NUM_ITER; ++i) {
		float t = float(i) * RECI_NUM_ITER_F;
		vec4 w = spectrumOffset(t);
		sumw += w;
		sumcol += w * texture2D(u_texture0, barrelDistortion(uv, 0.6 * MAX_DISTORT * t));
	}

	gl_FragColor = sumcol / sumw;
}