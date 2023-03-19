#version 330 core

in vec2 fPosition;

uniform vec3 lightPosition;

const float lightIntensity = 50;

void main() {
  vec2 distance = fPosition.xy - lightPosition.xy;
  float lightStrength =
    1 / (sqrt(distance.x * distance.x + distance.y * distance.y + lightIntensity * lightIntensity) - lightIntensity);
  gl_FragColor = vec4(lightStrength);
}

