#version 330 core

layout(location = 0) in vec2 aPosition;

uniform mat4 MVPMatrix;
uniform vec2 lightPosition;

out vec2 fPosition;
out vec2 fLightPosition;

void main() {
  fLightPosition = lightPosition; // = (MVPMatrix * vec4(lightPosition, 0., 1.)).xy;
  fPosition = aPosition; // screenPos.xy;
	gl_Position = vec4(aPosition, 0., 1.);
}

