attribute vec4 pPosition;
varying  vec4 vColor;
attribute vec4 aColor;

void main() {
  gl_Position = pPosition;
  vColor=aColor;
}