#version 330 core

layout(location = 0) in vec2 aPosition;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

out vec2 fPosition;

void main() {
  vec4 lightPos_world = vec4(aPosition.xy, 0, 1);
  gl_Position = projectionMatrix * viewMatrix * lightPos_world;
  fPosition = aPosition.xy;
}

