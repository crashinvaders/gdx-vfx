// Originally based on
// https://www.shadertoy.com/view/MtlyDX

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform vec2 u_resolution;
uniform float u_time;
varying vec2 v_texCoords;

vec3 scanline(vec2 coord, vec3 screen) {
    const float scale = 0.66;
    const float amt = 0.02; // intensity of effect
    const float spd = 1.0; // speed of scrolling rows transposed per second

	screen.rgb += vec3(sin((coord.y / scale - (u_time * spd * 6.28))) * amt);
	return screen;
}

vec2 fisheye(vec2 uv, float str) {
    vec2 neg1to1 = uv;
    neg1to1 = (neg1to1 - 0.5) * 2.0;

    vec2 offset;
    offset.x = ( pow(neg1to1.y,2.0)) * str * (neg1to1.x);
    offset.y = ( pow(neg1to1.x,2.0)) * str * (neg1to1.y);

    return uv + offset;
}

vec3 channelSplit(sampler2D tex, vec2 coord) {
    const float spread = 0.008;
	vec3 frag;
	frag.r = texture2D(tex, vec2(coord.x - spread * sin(u_time), coord.y)).r;
	frag.g = texture2D(tex, vec2(coord.x, 					     coord.y)).g;
	frag.b = texture2D(tex, vec2(coord.x + spread * sin(u_time), coord.y)).b;
	return frag;
}

void main() {
//	vec2 uv = fragCoord.xy / iResolution.xy;
	vec2 uv = v_texCoords;
	vec2 fisheyeUV = fisheye(uv, 0.03);
	vec3 channelSplit = channelSplit(u_texture0, fisheyeUV);
	vec2 screenSpace = fisheyeUV * u_resolution.xy;
	vec3 scanline = scanline(screenSpace, channelSplit);
	gl_FragColor = vec4(scanline, 1.0);
}