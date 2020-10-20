attribute vec4 vPosition;
uniform mat4 vMatrix;
varying  vec4 vColor;
attribute vec4 aColor;

attribute vec2 vCoordinate;
varying vec2 aCoordinate;

void main() {
  gl_Position = vMatrix*vPosition;
  vColor=aColor;

  aCoordinate=vCoordinate;

}