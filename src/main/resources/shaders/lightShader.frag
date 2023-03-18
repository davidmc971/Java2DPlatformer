#version 330 core

in vec2 fLightPosition;
in vec2 fPosition;

const float lightIntensity = 1;

void main() {
  vec2 distance = (fPosition - fLightPosition) * 0.8;
  float lightStrength =
    1 / (exp(distance.x * distance.x + distance.y * distance.y + lightIntensity * lightIntensity) - lightIntensity);
  gl_FragColor = vec4(vec3(1, 1, 1), lightStrength);
}

