// http://stackoverflow.com/a/12481351/3802890

#ifdef GL_ES
	#define PRECISION highp
	precision PRECISION float;
#else
	#define PRECISION
#endif

attribute vec4 a_position;
attribute vec4 a_texCoord0;

uniform float u_texelWidth;
uniform float u_texelHeight;

varying vec2 textureCoordinate;
varying vec2 leftTextureCoordinate;
varying vec2 rightTextureCoordinate;

varying vec2 topTextureCoordinate;
varying vec2 topLeftTextureCoordinate;
varying vec2 topRightTextureCoordinate;

varying vec2 bottomTextureCoordinate;
varying vec2 bottomLeftTextureCoordinate;
varying vec2 bottomRightTextureCoordinate;

void main() {
    gl_Position = a_position;

    vec2 widthStep = vec2(u_texelWidth, 0.0);
    vec2 heightStep = vec2(0.0, u_texelHeight);
    vec2 widthHeightStep = vec2(u_texelWidth, u_texelHeight);
    vec2 widthNegativeHeightStep = vec2(u_texelWidth, -u_texelHeight);

    textureCoordinate = a_texCoord0.xy;
    leftTextureCoordinate = a_texCoord0.xy - widthStep;
    rightTextureCoordinate = a_texCoord0.xy + widthStep;

    topTextureCoordinate = a_texCoord0.xy - heightStep;
    topLeftTextureCoordinate = a_texCoord0.xy - widthHeightStep;
    topRightTextureCoordinate = a_texCoord0.xy + widthNegativeHeightStep;

    bottomTextureCoordinate = a_texCoord0.xy + heightStep;
    bottomLeftTextureCoordinate = a_texCoord0.xy - widthNegativeHeightStep;
    bottomRightTextureCoordinate = a_texCoord0.xy + widthHeightStep;
}