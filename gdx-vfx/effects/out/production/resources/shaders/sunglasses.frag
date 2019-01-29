#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
varying vec2 v_texCoords;

uniform float u_intensity;

float adjust(float val, float thr1, float rate1, float thr2, float rate2) {
    float dif = 0.0;
    if (val > thr1) {
        val -= min(val - thr1, thr2 - thr1) * rate1;
    }
    if (val > thr2) {
        dif = (val - thr2) * rate2;
    }
    val -= dif;
    return val;
}

vec3 sepia(vec3 c, float adjust) {
    vec3 color = vec3(c);
    color.r = min(1.0, (color.r * (1.0 - (0.607 * adjust))) + (color.g * (0.769 * adjust)) + (color.b * (0.189 * adjust)));
    color.g = min(1.0, (color.r * (0.349 * adjust)) + (color.g * (1.0 - (0.314 * adjust))) + (color.b * (0.168 * adjust)));
    color.b = min(1.0, (color.r * (0.272 * adjust)) + (color.g * (0.534 * adjust)) + (color.b * (1.0 - (0.869 * adjust))));
    return color;
}

void main() {
	vec4 pointC = texture2D(u_texture0, v_texCoords);
	float contrast = 1.0 + 0.7 * u_intensity;
	float rate1 = 0.5 * u_intensity;
	float rate2 = 0.7 * u_intensity;
	pointC.r = adjust(pointC.r, 0.2, rate1, 0.5, rate2);
    pointC.g = adjust(pointC.g, 0.2, rate1, 0.5, rate2);
    pointC.b = adjust(pointC.b, 0.2, rate1, 0.5, rate2);
	pointC = vec4((pointC.rgb - vec3(0.5)) * contrast + vec3(0.5), 1.0);

	vec4 pointC2 = texture2D(u_texture0, v_texCoords);
	vec4 sep = vec4(sepia(pointC2.rgb, 0.75 * u_intensity), 1.0);
	gl_FragColor = mix(sep, pointC, 0.5);

    //gl_FragColor = pointC;
}