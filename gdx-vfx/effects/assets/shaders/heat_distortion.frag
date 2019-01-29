#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform sampler2D u_texture1;
uniform float u_time;
uniform float u_intensity;

varying vec2 v_texCoords;

float correctOffset(float value, float coord) {
    if (coord < 0.1 && value > 0.0) {
        value *= coord * 10.0;
    } else if (coord > 0.9 && value < 0.0) {
        value *= (1.0 - coord) * 10.0;
    }
    return value;
}

void main()
{
    float t = u_time;
    vec2 offset = texture2D(u_texture1, v_texCoords.xy + vec2(sin(u_time), cos(u_time))).rg;
    offset.x = correctOffset(offset.x, v_texCoords.x);
    offset.y = correctOffset(offset.y, v_texCoords.y);
	gl_FragColor = texture2D(u_texture0, v_texCoords.xy - u_intensity * 0.03 * offset);
}
