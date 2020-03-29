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
// https://www.shadertoy.com/view/4ltSDB

//#version 120
#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
uniform sampler2D u_texture0;
uniform float u_amount;
uniform float u_speed;
uniform float u_time;

void main ()
{
    vec2 uv = v_texCoords;

    uv.y += (cos((uv.y + (u_time * 0.04 * u_speed)) * 45.0) * 0.0019 * u_amount) + (cos((uv.y + (u_time * 0.1 * u_speed)) * 10.0) * 0.002 * u_amount);

    uv.x += (sin((uv.y + (u_time * 0.07 * u_speed)) * 15.0) * 0.0029 * u_amount) + (sin((uv.y + (u_time * 0.1 * u_speed)) * 15.0) * 0.002 * u_amount);

    gl_FragColor = texture2D(u_texture0, uv);
}