precision mediump float;
varying vec4 vColor;

varying vec2 aCoordinate;
uniform sampler2D vTexture;

void main() {

  gl_FragColor = texture2D(vTexture, aCoordinate)+vColor;

}