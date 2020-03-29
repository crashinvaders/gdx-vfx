#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

#ifndef THRESHOLD_TYPE
	#error Please define THRESHOLD_TYPE
#endif

#define RGBA 0
#define RGB 1
#define ALPHA_PREMULTIPLIED 2

uniform PRECISION sampler2D u_texture0;
uniform float u_threshold;
uniform float u_thresholdInv;
varying vec2 v_texCoords;

void main() {
	vec4 tex = texture2D(u_texture0, v_texCoords);

#if THRESHOLD_TYPE == RGBA
	gl_FragColor = (tex - vec4(u_threshold)) * u_thresholdInv;

#elif THRESHOLD_TYPE == RGB
	gl_FragColor.a = tex.a;
	gl_FragColor.rgb = (tex.rgb - u_threshold) * u_thresholdInv;

#elif THRESHOLD_TYPE == ALPHA_PREMULTIPLIED
	gl_FragColor = (tex.rgb - u_threshold) * u_thresholdInv * tex.a, tex.a;

#else
	#error Unexpected THRESHOLD_TYPE value

#endif
}