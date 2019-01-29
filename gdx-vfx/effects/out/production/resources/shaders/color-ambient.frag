#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
varying vec2 v_texCoords;

uniform float u_intensity;

void main() {
	vec4 pointC = texture2D(u_texture0, v_texCoords);
    float brightness = (pointC.r + pointC.g + pointC.b) / 3.0;
    float intensity = u_intensity * smoothstep(1.0, 0.0, brightness * brightness);
    pointC.g *= 1.0 - 0.2 * intensity;
    pointC.r *= 1.0 - 0.8 * intensity;
    pointC.b *= 1.0 - 0.8 * intensity;
	gl_FragColor = pointC;
}