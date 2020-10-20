#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 aCoordinate;
uniform samplerExternalOES vTexture;


//layout(location = 0) out vec4 o_FragColor;

void main() {
    gl_FragColor = texture2D( vTexture, aCoordinate );
}

//precision mediump float;
//
//uniform sampler2D vTexture;
//varying vec2 aCoordinate;
//
//void main(){
//    gl_FragColor=texture2D(vTexture,aCoordinate);
//}