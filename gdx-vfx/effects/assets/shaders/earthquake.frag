/*******************************************************************************
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

// Originally based on
// https://www.shadertoy.com/view/wsBXWW

#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture0;
uniform float u_time;

uniform float u_amount;
uniform float u_speed;

vec3 random3(vec3 c) {
	float j = 4096.0*sin(dot(c,vec3(17.0, 59.4, 15.0)));
	vec3 r;
	r.z = fract(512.0*j);
	j *= .125;
	r.x = fract(512.0*j);
	j *= .125;
	r.y = fract(512.0*j);
	return r;
}
const float F3 =  0.3333333;
const float G3 =  0.1666667;

float simplex3d(vec3 p) {
	 vec3 s = floor(p + dot(p, vec3(F3)));
	 vec3 x = p - s + dot(s, vec3(G3));

	 vec3 e = step(vec3(0.0), x - x.yzx);
	 vec3 i1 = e*(1.0 - e.zxy);
	 vec3 i2 = 1.0 - e.zxy*(1.0 - e);

	 vec3 x1 = x - i1 + G3;
	 vec3 x2 = x - i2 + 2.0*G3;
	 vec3 x3 = x - 1.0 + 3.0*G3;

	 vec4 w, d;

	 w.x = dot(x, x);
	 w.y = dot(x1, x1);
	 w.z = dot(x2, x2);
	 w.w = dot(x3, x3);

	 w = max(0.6 - w, 0.0);

	 d.x = dot(random3(s)-.5, x);
	 d.y = dot(random3(s + i1)-.5, x1);
	 d.z = dot(random3(s + i2)-.5, x2);
	 d.w = dot(random3(s + 1.0)-.5, x3);

	 w *= w;
	 w *= w;
	 d *= w;

	 return dot(d, vec4(52.0));
}

void main()
{
   	vec3 p3 = vec3(0,0, u_time*u_speed)*8.0+8.0;
    vec2 noise = vec2(simplex3d(p3),simplex3d(p3+10.));
	gl_FragColor = vec4(texture2D(u_texture0, v_texCoords+noise*u_amount*0.1 ).rgb, 1.0);
}