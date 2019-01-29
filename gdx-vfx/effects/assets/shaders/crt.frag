#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

const float SCANLINE_FACTOR_0 = 1.3;
const float SCANLINE_FACTOR_1 = 0.5;
const float SCANLINE_F_BASELINE = min(SCANLINE_FACTOR_0, SCANLINE_FACTOR_1);
const float SCANLINE_F_DIF = max(SCANLINE_FACTOR_0, SCANLINE_FACTOR_1) - SCANLINE_F_BASELINE;

uniform sampler2D u_texture0;
uniform vec2 u_resolution;
varying vec2 v_texCoords;

void main() {
    vec4 color = texture2D(u_texture0, v_texCoords);

//    // Hard crossline pattern.
//    float x = u_resolution.x * v_texCoords.x;
//    float y = u_resolution.y * v_texCoords.y;
//    float componentX = SCANLINE_F_BASELINE + SCANLINE_F_DIF * step(1.0, mod(x, 2.0));
//    float componentY = SCANLINE_F_BASELINE + SCANLINE_F_DIF * step(1.0, mod(y, 2.0));
//    float component = (componentX + componentY) * (componentX * componentY) * 1.2;
//    color = color * vec4(component, component, component, 1.0);

//    // Hard vertical line.
//    float x = u_resolution.x * v_texCoords.x;
//    float component = SCANLINE_F_BASELINE + SCANLINE_F_DIF * step(1.0, mod(x, 2.0));
//    color = color * vec4(component, component, component, 1.0);

    // Hard horizontal line.
    float y = u_resolution.y * v_texCoords.y;
    float component = SCANLINE_F_BASELINE + SCANLINE_F_DIF * step(1.0, mod(y, 2.0));
    color = color * vec4(component, component, component, 1.0);

//    // Smooth horizontal line.
//    float y = u_resolution.y * v_texCoords.y;
//    float factor = abs(mod(y, 2.0) - 1.0);
//    float componentValue = mix(SCANLINE_FACTOR_0, SCANLINE_FACTOR_1, factor);
//    color = color * vec4(componentValue, componentValue, componentValue, 1.0);

    gl_FragColor = color;
}