precision mediump float;

uniform vec4 u_Color;

varying vec4 v_Ambient;
varying vec4 v_Diffuse;
varying vec4 v_Specular;

void main()
{
    //将计算出的颜色给此片元
    gl_FragColor = u_Color*v_Ambient+u_Color*v_Specular+u_Color*v_Diffuse;//给此片元颜色值
//    gl_FragColor = u_Color;
}