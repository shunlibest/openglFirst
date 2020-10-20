precision mediump float;
varying vec4 vColor;
void main() {
   if (length(gl_PointCoord - vec2(0.5)) > 0.5)
       discard;
   else
       gl_FragColor = vec4(vColor.rgb, (1.0 - length(gl_PointCoord - vec2(0.5))*2.0));

    // gl_FragColor=vColor;
}
