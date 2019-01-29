#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
varying vec2 v_texCoords;

uniform float u_time;
uniform float u_intensity;


void main() {
    vec3 raintex = texture2D(u_texture1, vec2(v_texCoords.x * 0.65, v_texCoords.y * 0.1 + u_time * 0.0625)).rgb/16.0;
    vec2 where = (v_texCoords.xy - raintex.xy * u_intensity * 0.5);
    vec3 result = texture2D(u_texture0, where).rgb;

	gl_FragColor = vec4(result, 1.0);
}