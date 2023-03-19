#version 330 core

in vec2 UV;

uniform sampler2D renderedTexture;
uniform float alpha;

const float twoPi = 6.28318530718;
const float directions = 16;
const float quality = 4;
const float size = 8;

void blur() {
  vec2 radius = vec2((alpha - 2) * size);
  vec4 color = texture(renderedTexture, UV);
  for (float d = 0; d < twoPi; d += twoPi / directions) {
    for (float i = 1.0; i <= 1; i += 1 / quality) {
      vec2 offset = vec2(cos(d), sin(d)) * radius * i;
      offset.x /= 1280;
      offset.y /= 720;
      color += texture(renderedTexture, UV + offset);
    }
  }
  color /= quality * directions - 15;
  gl_FragColor = vec4(color.rgb, 1 - texture(renderedTexture, UV).a);
}

void main() {
    if (alpha > 1) {
      blur();
  } else  {
    vec4 texColor = texture(renderedTexture, UV);
    if (alpha < 0) {
      gl_FragColor = texColor;
    } else {    
      gl_FragColor = vec4(vec3(alpha * texColor.rgb + 1 - alpha), 1);
    }
  }
  
}