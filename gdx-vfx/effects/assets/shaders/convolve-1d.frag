/*******************************************************************************
 * Copyright 2012 bmanuel
 * Copyright 2019 metaphore
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
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