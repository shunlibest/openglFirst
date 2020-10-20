precision mediump float;
varying vec4 vColor;
//uniform vec4 vColor;

uniform sampler2D vTexturex;


void main() {
  gl_FragColor = vColor;
}