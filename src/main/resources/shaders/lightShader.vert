#version 330 core

layout(location = 0) in vec2 aPosition;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec2 lightPosition;
uniform float lightIndex;

out vec2 fLightPosition;
out vec2 fPosition;

void main() {
  fLightPosition = (projectionMatrix * viewMatrix * vec4(lightPosition, 0, 1)).xy;
  fPosition = aPosition.xy;
  gl_Position = vec4(aPosition.xy, -lightIndex, 1);
}

