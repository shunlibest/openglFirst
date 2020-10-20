precision mediump float;
// varying vec2 textureCoordinate;
// uniform sampler2D vTexture;

varying vec4 vColor;
void main() {
    // gl_FragColor = texture2D( vTexture, textureCoordinate );

   gl_FragColor = vColor;

}
