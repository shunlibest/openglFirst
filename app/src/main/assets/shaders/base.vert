attribute vec4 vPosition;
uniform mat4 vMatrix;

// attribute vec2 vCoord;
// varying vec2 textureCoordinate;

attribute vec4 aColor;
varying  vec4 vColor;

void main(){
    gl_Position = vMatrix*vPosition;
    // gl_Position = vPosition;
    // textureCoordinate = vCoord;

    vColor=aColor;

}