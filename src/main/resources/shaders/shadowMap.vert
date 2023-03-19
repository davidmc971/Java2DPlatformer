#version 330 core

layout(location = 0) in vec3 aPosition;
layout(location = 1) in float aMoveable;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 lightPosition;

void main() {
  vec2 pos = aPosition.xy;
  if (aMoveable > 0) {
      vec2 distance = pos - lightPosition.xy;
      vec2 scalarDistance = distance / sqrt(distance.x * distance.x + distance.y * distance.y);
      pos += scalarDistance * 100000;
  }
  vec4 worldPos = vec4(pos.xy, 0, 1);
  gl_Position = projectionMatrix * viewMatrix * worldPos;
}

