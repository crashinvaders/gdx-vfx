#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform float u_time;
uniform float u_intensity;

varying vec2 v_texCoords;

const float maxOffset = 1.0 / 30.0;

float calcXOffset(vec2 coords) {
    float xOffset = sin(coords.y * 30.0 + 3.0 * u_time) * maxOffset;

    float xShift = abs(0.5 - coords.x);
    float k = (0.5 - xShift) * 2.0;
    k = 1.0 - k;
    k = 1.0 - k * k * k;

    return k * u_intensity * xOffset;
}

float calcYOffset(vec2 coords) {
    float yOffset = sin(coords.x * 10.0 + u_time) / 15.0;

    float yShift = abs(0.5 - coords.y);
    float k = (0.5 - yShift) * 2.0;
    k = 1.0 - k;
    k = 1.0 - k * k * k;

    return k * u_intensity * yOffset;
}

void main() {
    vec2 coords = vec2(v_texCoords);
	float xOffset = calcXOffset(coords);
	float yOffset = calcYOffset(coords);
	coords.x += xOffset;
	coords.y += yOffset;
	gl_FragColor = texture2D(u_texture0, coords);
}
