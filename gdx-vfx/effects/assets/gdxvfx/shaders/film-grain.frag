// Originally based on https://www.shadertoy.com/view/4ljfRG

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
	precision PRECISION int;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;

varying vec2 v_texCoords;

uniform float u_seed;
uniform float u_noiseAmount;

void main() {
	vec2 uv = v_texCoords;
    vec4 color = texture2D(u_texture0, v_texCoords);

    float n = fract(sin(dot(uv, vec2(u_seed + 12.9898, 78.233))) * 43758.5453);
    color.rgb *= (1.0 - u_noiseAmount + n * u_noiseAmount) * 1.1;
	gl_FragColor = color;
}