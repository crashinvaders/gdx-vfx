#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

varying vec2 v_texCoords;

uniform PRECISION sampler2D u_texture0;
uniform PRECISION vec2 u_texel;
uniform PRECISION float u_offset;

void main() {
    vec2 axisOffset = u_texel * u_offset;   // axis tap distance (1 source texel)
    vec2 diagonalOffset = axisOffset * 0.5; // diagonal tap distance (0.5 source texel)

    vec4 sum = vec4(0.0);
    sum += texture2D(u_texture0, v_texCoords + vec2(-axisOffset.x, 0.0));
    sum += texture2D(u_texture0, v_texCoords + vec2(axisOffset.x, 0.0));
    sum += texture2D(u_texture0, v_texCoords + vec2(0.0, -axisOffset.y));
    sum += texture2D(u_texture0, v_texCoords + vec2(0.0, axisOffset.y));
    sum += texture2D(u_texture0, v_texCoords + vec2(-diagonalOffset.x, -diagonalOffset.y)) * 2.0;
    sum += texture2D(u_texture0, v_texCoords + vec2(diagonalOffset.x, -diagonalOffset.y)) * 2.0;
    sum += texture2D(u_texture0, v_texCoords + vec2(-diagonalOffset.x, diagonalOffset.y)) * 2.0;
    sum += texture2D(u_texture0, v_texCoords + vec2(diagonalOffset.x, diagonalOffset.y)) * 2.0;

    gl_FragColor = sum / 12.0;
}