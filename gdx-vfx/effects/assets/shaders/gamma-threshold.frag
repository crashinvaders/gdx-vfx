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