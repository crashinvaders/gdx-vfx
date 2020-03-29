#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

#ifndef LENGTH
	#error Please define LENGTH
#endif

varying vec2 v_texCoords;
uniform PRECISION sampler2D u_texture0;
uniform PRECISION float u_sampleWeights[LENGTH];
uniform PRECISION vec2 u_sampleOffsets[LENGTH];

void main() {
	vec4 color = vec4(0);

	// Combine a number of weighted image filter taps.
	for (int i = 0; i < LENGTH; i++) {
		color += texture2D(u_texture0, v_texCoords + u_sampleOffsets[i]) * u_sampleWeights[i];
	}

	gl_FragColor = color;
}