package io.github.davidmc971.java2dplatformer.rendering;

import org.lwjgl.opengl.GL20;

public enum ShaderType {
  FRAGMENT(GL20.GL_FRAGMENT_SHADER, "frag"),
  VERTEX(GL20.GL_VERTEX_SHADER, "vert");

  public final int glShaderTypeId;
  public final String fileExtension;

  private ShaderType(int glShaderTypeId, String fileExtension) {
    this.glShaderTypeId = glShaderTypeId;
    this.fileExtension = fileExtension;
  }

  public static ShaderType inferFromPath(String path) throws CouldNotInferShaderTypeException {
    String[] splitPath = path.split("\\.");
    if (splitPath.length > 0) {
      for (ShaderType shaderType : ShaderType.values()) {
        if (shaderType.fileExtension.equalsIgnoreCase(splitPath[splitPath.length - 1])) {
          return shaderType;
        }
      }
    }
    throw new CouldNotInferShaderTypeException();
  }

  public static class CouldNotInferShaderTypeException extends Exception {
    public CouldNotInferShaderTypeException() {
      super("Shader type could not be inferred.");
    }
  }
}
