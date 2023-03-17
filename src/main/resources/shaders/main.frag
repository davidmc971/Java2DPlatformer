#version 330
varying vec4 vColor;
uniform sampler2D[] textureSampler;

in vec2 fUVCoords;
in float fTextureId;

layout(location = 0) out vec4 colorOut;

// FIXME: This is annoying, OpenGL 3.3 should have another way of doing this...
vec4 samplerLookup(float textureId, vec2 uvCoords) {
    switch (int(fTextureId)) {
        case -1: return vec4(1.);
        case 0: return texture(textureSampler[0], uvCoords);
        case 1: return texture(textureSampler[1], uvCoords);
        case 2: return texture(textureSampler[2], uvCoords);
        case 3: return texture(textureSampler[3], uvCoords);
        case 4: return texture(textureSampler[4], uvCoords);
        case 5: return texture(textureSampler[5], uvCoords);
        case 6: return texture(textureSampler[6], uvCoords);
        case 7: return texture(textureSampler[7], uvCoords);
        case 8: return texture(textureSampler[8], uvCoords);
        case 9: return texture(textureSampler[9], uvCoords);
        case 10: return texture(textureSampler[10], uvCoords);
        case 11: return texture(textureSampler[11], uvCoords);
        case 12: return texture(textureSampler[12], uvCoords);
        case 13: return texture(textureSampler[13], uvCoords);
        case 14: return texture(textureSampler[14], uvCoords);
        case 15: return texture(textureSampler[15], uvCoords);
        case 16: return texture(textureSampler[16], uvCoords);
        case 17: return texture(textureSampler[17], uvCoords);
        case 18: return texture(textureSampler[18], uvCoords);
        case 19: return texture(textureSampler[19], uvCoords);
        case 20: return texture(textureSampler[20], uvCoords);
        case 21: return texture(textureSampler[21], uvCoords);
        case 22: return texture(textureSampler[22], uvCoords);
        case 23: return texture(textureSampler[23], uvCoords);
        case 24: return texture(textureSampler[24], uvCoords);
        case 25: return texture(textureSampler[25], uvCoords);
        case 26: return texture(textureSampler[26], uvCoords);
        case 27: return texture(textureSampler[27], uvCoords);
        case 28: return texture(textureSampler[28], uvCoords);
        case 29: return texture(textureSampler[29], uvCoords);
        case 30: return texture(textureSampler[30], uvCoords);
        case 31: return texture(textureSampler[31], uvCoords);
        default: return texture(textureSampler[0], uvCoords);
    }
}

void main(void) {
    vec4 texColor = samplerLookup(fTextureId, fUVCoords);
    float grey = 0.2126 * texColor.r + 0.7152 * texColor.g + 0.0722 * texColor.b;
    colorOut = (((grey * texColor) * 0.8) + (texColor * 0.5)) * vColor;
}