#version 330
varying vec4 vColor;
layout(location = 0) out vec4 colorOut;
void main(void) {
    colorOut = vColor;
}
