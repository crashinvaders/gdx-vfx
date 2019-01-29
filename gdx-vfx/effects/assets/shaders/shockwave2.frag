// Grabbed from http://www.geeks3d.com/20091116/shader-library-2d-shockwave-post-processing-filter-glsl/

#ifdef GL_ES
	#define PRECISION mediump
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform float u_time;
//uniform float u_aspectRatio;
uniform vec2 u_center;
uniform vec3 u_shock_params; // 10.0, 0.8, 0.1
//const vec3 u_shock_params = vec3(10.0, 0.2, 0.1);

varying vec2 v_texCoords;

void main() {
  vec2 uv = v_texCoords.xy;
  vec2 texCoord = uv;
  float distance = distance(uv, u_center);

  if ( (distance <= (u_time + u_shock_params.z)) &&
       (distance >= (u_time - u_shock_params.z)) ) {
    float diff = (distance - u_time);
    float powDiff = 1.0 - pow(abs(diff*u_shock_params.x), u_shock_params.y);
    float diffTime = diff  * powDiff;
    vec2 diffUV = normalize(uv - u_center);
    texCoord = uv + (diffUV * diffTime);
  }
  gl_FragColor = texture2D(u_texture0, texCoord);
}

uniform sampler2D tex;
uniform float posX;
uniform float posY;
uniform float time;

uniform sampler2D tex;
uniform vec2 center;
uniform float time;

void main (void)
{
  float distance = distance(gl_TexCoord[0].st,center);
  if (distance <= time + 0.025 && distance >= time - 0.025) {

    float ecart = (distance - time); // value between -0.02 & 0.02

    float powEcart = 1.0-pow(abs(ecart*40.0),0.4); // value between -1 & 1 (because 0.02 * 50 = 1)

    float ecartTime = ecart  * powEcart; // value between -0.02 & 0.02

    vec2 diff = normalize(gl_TexCoord[0].st - center); // get the direction

    vec2 newTexCoord = gl_TexCoord[0].st + (diff * ecartTime);

    gl_FragColor = texture2D(tex, newTexCoord.st) * gl_Color;
  } else {
    discard; //gl_FragColor = texture2D(tex, gl_TexCoord[0].st) * gl_Color;
  }
}