#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

uniform PRECISION sampler2D u_texture0;
uniform PRECISION sampler2D u_texture1;
uniform float u_src0Intensity;
uniform float u_src1Intensity;
// 0 = totally desaturated
// 1 = saturation unchanged
// higher = increase saturation
uniform float u_src0Saturation;
uniform float u_src1Saturation;

varying vec2 v_texCoords;

// The constants 0.3, 0.59, and 0.11 are chosen because the
// human eye is more sensitive to green light, and less to blue.
const vec3 GRAYSCALE = vec3(0.3, 0.59, 0.11);

vec3 adjustSaturation(vec3 color, float saturation) {
	vec3 grey = vec3(dot(color, GRAYSCALE));
	return mix(grey, color, saturation);
}

void main() {
	// lookup inputs
	vec4 src0 = texture2D(u_texture0, v_texCoords) * u_src0Intensity;
	vec4 src1 = texture2D(u_texture1, v_texCoords) * u_src1Intensity;

	// adjust color saturation and intensity
	src0.rgb = adjustSaturation(src0.rgb, u_src0Saturation);
	src1.rgb = adjustSaturation(src1.rgb, u_src1Saturation);

	// darken the base image in areas where ther's a lot of bloom
	// to prevent things looking excessively burned-out
	//original *= (1.0 - clamp(bloom, 0.0, 1.0));
	src0 *= (1.0 - src1);

	// combine
	gl_FragColor = src0 + src1;
}