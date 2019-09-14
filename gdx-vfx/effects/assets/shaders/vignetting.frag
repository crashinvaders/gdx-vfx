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

varying vec2 v_texCoords;

uniform sampler2D u_texture0;
uniform float u_vignetteIntensity;
uniform float u_vignetteX;
uniform float u_vignetteY;
uniform float u_centerX;
uniform float u_centerY;

#ifdef CONTROL_SATURATION
	const vec3 grayscale = vec3(0.3, 0.59, 0.11);

	uniform float u_saturation;
	uniform float u_saturationMul;

	// 0 = totally desaturated
	// 1 = saturation unchanged
	// higher = increase saturation
	vec3 adjustSaturation(vec3 color, float saturation) {
		vec3 grey = vec3(dot(color, grayscale));
		return mix(grey, color, saturation);	// correct
	}
#endif

void main() {
	vec3 rgb = texture2D(u_texture0, v_texCoords).xyz;
	float d = distance(v_texCoords, vec2(u_centerX, u_centerY));
	float factor = smoothstep(u_vignetteX, u_vignetteY, d);
	rgb = rgb * factor + rgb * (1.0 - factor) * (1.0 - u_vignetteIntensity);

#ifdef CONTROL_SATURATION
	rgb = adjustSaturation(rgb, u_saturation) * u_saturationMul;
#endif

	gl_FragColor = vec4(rgb, 1);
}