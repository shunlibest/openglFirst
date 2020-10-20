attribute vec4 aPosition;
uniform mat4 uMatrix;
uniform float uPonitSize;

// attribute vec2 vCoord;
// varying vec2 textureCoordinate;

attribute vec4 aColor;
varying  vec4 vColor;


void main()
{

    gl_Position = uMatrix*aPosition;
    gl_PointSize = uPonitSize;

    vColor=aColor;

}
