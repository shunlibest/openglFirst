precision mediump float;
varying vec2 aCoordinate;
uniform sampler2D vTexturex;
void main() {
    gl_FragColor = texture2D( vTexturex, aCoordinate );
//    gl_FragColor =aCoordinate;
}
