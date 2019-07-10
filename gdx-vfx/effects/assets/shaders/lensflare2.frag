// Lens flare implementation by Toni Sagrista
// From John Chapman's article http://john-chapman-graphics.blogspot.co.uk/2013/02/pseudo-lens-flare.html
//#version 120
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

const float GHOST_DISPERSAL = 0.25; // dispersion factor
const float DISTORTION = 1.0;

// Unprocessed image
uniform sampler2D u_texture0;
// Lens color
uniform sampler2D u_texture1;

varying vec2 v_texCoords;

uniform vec2 u_viewportInverse;
uniform float u_haloWidth;

/*----------------------------------------------------------------------------*/
vec4 textureDistorted(
	sampler2D tex, 
	vec2 texcoord, 
	vec2 direction,
	vec3 distortion 
) {
	return vec4(
		texture2D(tex, texcoord + direction * distortion.r).r,
		texture2D(tex, texcoord + direction * distortion.g).g,
		texture2D(tex, texcoord + direction * distortion.b).b,
		1.0
	);
}

/*----------------------------------------------------------------------------*/
void main() {
    vec2 texcoord = -v_texCoords + vec2(1.0);
    vec2 texelSize = u_viewportInverse;
    
    // ghost vector to image centre:
    vec2 ghostVec = (vec2(0.5) - texcoord) * GHOST_DISPERSAL;
    vec2 haloVec = normalize(ghostVec) * u_haloWidth;
    	
    vec3 distortion = vec3(-texelSize.x * DISTORTION, 0.0, texelSize.x * DISTORTION);
    
    // sample ghosts:  
    vec4 result = vec4(0.0);
    for (int i = 0; i < GHOSTS; ++i) {
        vec2 offset = fract(texcoord + ghostVec * float(i));

        float weight = length(vec2(0.5) - offset) / length(vec2(0.5));
        weight = pow(1.0 - weight, 2.0);

        result += textureDistorted(
                    u_texture0,
                    offset,
                    normalize(ghostVec),
                    distortion
                ) * weight;
    }
    result *= texture2D(u_texture1, vec2(length(vec2(0.5) - texcoord) / length(vec2(0.5))));
  
    //	sample halo:
    float weight = length(vec2(0.5) - fract(texcoord + haloVec)) / length(vec2(0.5));
    weight = pow(1.0 - weight, 2.0);
    result += textureDistorted(
	    u_texture0,
	    fract(texcoord + haloVec),
	    normalize(ghostVec),
	    distortion
    ) * weight;
    
    gl_FragColor = result;
}
