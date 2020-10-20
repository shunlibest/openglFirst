attribute vec4 vPosition;
attribute vec2 vCoordinate;
//uniform mat4 vMatrix;

varying vec2 aCoordinate;

void main(){
//    gl_Position=vMatrix*vPosition;
    gl_Position=
    mat4(
    0,-1,0,0,
    1,0,0,0,
    0,0,1,0,
    0,0,0,1
    )*

    vPosition;
    aCoordinate=vCoordinate;
}