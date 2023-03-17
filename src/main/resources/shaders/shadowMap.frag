#version 330 core

layout(location = 0) out vec4 colorOut;

in vec2 fPosition;

void main() {
  colorOut = vec4(0, 0, 0, 0.25);
}

