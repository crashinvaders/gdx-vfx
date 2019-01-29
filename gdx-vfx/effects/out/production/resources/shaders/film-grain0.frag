// Originally based on https://www.shadertoy.com/view/4sXSWs

#ifdef GL_ES
	#define PRECISION highp
	precision PRECISION float;
#else
	#define PRECISION
#endif

const float STRENGTH = 16.0;

varying vec2 v_texCoords;
uniform sampler2D u_texture0;
uniform float u_seed;

void main() {
	vec2 uv = v_texCoords;
    vec4 color = texture2D(u_texture0, v_texCoords);

    float x = (uv.x + 4.0) * (uv.y + 4.0) * (u_seed * 10.0);
    vec4 grain = vec4(mod((mod(x, 13.0) + 1.0) * (mod(x, 123.0) + 1.0), 0.01) - 0.005) * STRENGTH;

//    grain = 1.0 - grain;
//	  gl_FragColor = color * grain;

    gl_FragColor = color + grain;
}