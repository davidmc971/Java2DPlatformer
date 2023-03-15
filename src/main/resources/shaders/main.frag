#version 330
varying vec4 vColor;
uniform sampler2D textureSampler;

in vec2 fUVCoords;

layout(location = 0) out vec4 colorOut;

void main(void) {
    colorOut = texture(textureSampler, fUVCoords);
}
