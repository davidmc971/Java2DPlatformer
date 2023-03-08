#version 330
uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;

varying vec4 vColor;

void main(void) {
    vColor = aColor;
    gl_Position = projection * view * model * vec4(aPosition.xyz, 1);
}
