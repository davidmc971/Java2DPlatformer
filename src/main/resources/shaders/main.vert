#version 330
uniform mat4 projection;
uniform mat4 model;
uniform mat4 view;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aUVCoords;
layout(location = 3) in float aTextureId;

varying vec4 vColor;
out vec2 fUVCoords;
out float fTextureId;

void main(void) {
    vColor = aColor;
    fUVCoords = aUVCoords;
    fTextureId = aTextureId;
    gl_Position = projection * view * model * vec4(aPosition.xyz, 1);
}
