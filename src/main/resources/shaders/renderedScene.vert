#version 330 core

layout(location = 0) in vec2 aPosition;
layout(location = 1) in vec2 aUVCoords;

out vec2 UV;

void main(){
	gl_Position = vec4(aPosition.xy, 0., 1.);
	UV = aUVCoords;
}