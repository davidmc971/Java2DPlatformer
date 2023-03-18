#version 330 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in float aMoveable;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec2 lightPosition;
uniform float lightIndex;

void main() {
  vec2 pos = aPosition;
  if (aMoveable > 0) {
      vec2 distance = pos - lightPosition;
      vec2 scalarDistance = distance / sqrt(distance.x * distance.x + distance.y * distance.y);
      pos += scalarDistance * 100000;
  }

  gl_Position = vec4((projectionMatrix * viewMatrix * vec4(pos.xy, 0, 1)).xy, -lightIndex - 0.5, 1);
}

