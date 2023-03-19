#version 330 core

in vec2 fPosition;

uniform vec3 lightPosition;

const float radius = 10;
const float distanceDivisor = 40;

// Don't mind me playing around with lights, I earned it :^)

void main() {
  vec2 distance = fPosition.xy - lightPosition.xy;
  distance /= distanceDivisor;

  float dist = sqrt(distance.x * distance.x + distance.y * distance.y);

  float lightStrength = 0;
  // lightStrength =
  //   exp(distance.x * distance.x + distance.y * distance.y + lightIntensity * lightIntensity) - lightIntensity;
  // lightStrength = clamp(1.0 - (dist*dist)/(radius*radius), 0.0, 1.0);
  lightStrength = 1 / (1 + 4 * dist + 1.2 * dist * dist);
  // lightStrength *= lightStrength;
  gl_FragColor = vec4(lightStrength);
}

