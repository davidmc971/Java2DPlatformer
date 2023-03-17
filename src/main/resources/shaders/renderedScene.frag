#version 330 core

in vec2 UV;

layout(location = 0) out vec4 colorOut;

uniform sampler2D renderedTexture;

void main() {
  colorOut = texture(renderedTexture, UV);
}