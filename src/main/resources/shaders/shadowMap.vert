#version 330 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in float aMoveable;

uniform mat4 MVPMatrix;
uniform vec2 lightPosition;

out vec2 fPosition;

void main() {
  vec4 pos = MVPMatrix * vec4(aPosition.xy, 0., 1.);
  if (aMoveable > 0) {
      vec2 distance = pos.xy - lightPosition;
      vec2 scalarDistance = distance / sqrt(distance.x * distance.x + distance.y * distance.y);
      pos += vec4(scalarDistance * 1000, 0, 0);
  }
  gl_Position = pos;
	fPosition = pos.xy;
}

