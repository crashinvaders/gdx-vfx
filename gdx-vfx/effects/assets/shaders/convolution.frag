// http://stackoverflow.com/a/12481351/3802890

#ifdef GL_ES
	#define PRECISION highp
	precision PRECISION float;
#else
	#define PRECISION
#endif

uniform sampler2D u_texture0;
uniform mat3 u_convMat;

//varying vec2 v_texCoords;

varying vec2 textureCoordinate;
varying vec2 leftTextureCoordinate;
varying vec2 rightTextureCoordinate;

varying vec2 topTextureCoordinate;
varying vec2 topLeftTextureCoordinate;
varying vec2 topRightTextureCoordinate;

varying vec2 bottomTextureCoordinate;
varying vec2 bottomLeftTextureCoordinate;
varying vec2 bottomRightTextureCoordinate;

void main()
{
    vec4 bottomColor = texture2D(u_texture0, bottomTextureCoordinate);
    vec4 bottomLeftColor = texture2D(u_texture0, bottomLeftTextureCoordinate);
    vec4 bottomRightColor = texture2D(u_texture0, bottomRightTextureCoordinate);
    vec4 centerColor = texture2D(u_texture0, textureCoordinate);
    vec4 leftColor = texture2D(u_texture0, leftTextureCoordinate);
    vec4 rightColor = texture2D(u_texture0, rightTextureCoordinate);
    vec4 topColor = texture2D(u_texture0, topTextureCoordinate);
    vec4 topRightColor = texture2D(u_texture0, topRightTextureCoordinate);
    vec4 topLeftColor = texture2D(u_texture0, topLeftTextureCoordinate);

    vec4 resultColor = topLeftColor * u_convMat[0][0] + topColor * u_convMat[0][1] + topRightColor * u_convMat[0][2];
    resultColor += leftColor * u_convMat[1][0] + centerColor * u_convMat[1][1] + rightColor * u_convMat[1][2];
    resultColor += bottomLeftColor * u_convMat[2][0] + bottomColor * u_convMat[2][1] + bottomRightColor * u_convMat[2][2];

    gl_FragColor = resultColor;
}