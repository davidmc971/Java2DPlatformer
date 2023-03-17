#version 330 core

layout(location = 0) out vec4 colorOut;

in vec2 fPosition;
in vec2 fLightPosition;

const float dampen = 100;
const float lightIntensity = 1;

void main() {
  vec2 distance = (fPosition - fLightPosition) * vec2(dampen);
  float lightStrength =
    1 / (sqrt(distance.x * distance.x + distance.y * distance.y + lightIntensity * lightIntensity) - lightIntensity);
  colorOut = vec4(vec3(lightStrength), lightStrength);
}

