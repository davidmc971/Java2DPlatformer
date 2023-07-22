#version 330 core

layout(lines) in;
layout(triangle_strip, max_vertices = 4) out;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 lightPosition;

void emit(vec2 point) {
  gl_Position = projectionMatrix * viewMatrix * vec4(point, 0, 1);
  EmitVertex();
}

vec2 move(vec2 point) {
  vec2 pos = point;
  vec2 distance = pos - lightPosition.xy;
  vec2 scalarDistance =
      distance / sqrt(distance.x * distance.x + distance.y * distance.y);
  pos += scalarDistance * 1000;
  return pos;
}

void main() {
  vec2 p1 = gl_in[0].gl_Position.xy;
  vec2 p2 = gl_in[1].gl_Position.xy;

  emit(p1);
  emit(p2);
  emit(move(p1));
  emit(move(p2));
  EndPrimitive();
}
