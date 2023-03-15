#version 330
varying vec4 vColor;
uniform sampler2D textureSampler;

in vec2 fUVCoords;

layout(location = 0) out vec4 colorOut;

void main(void) {
    vec4 texColor = texture(textureSampler, fUVCoords);
    float grey = 0.21 * texColor.r + 0.71 * texColor.g + 0.07 * texColor.b;
    colorOut = (((grey * texColor) * 0.8) + (texColor * 0.5)) * vColor;
}
