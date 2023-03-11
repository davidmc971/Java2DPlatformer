package io.github.davidmc971.java2dplatformer.rendering;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL20;

public class ShaderProgram {
  public final int programId;
  private List<Shader> attachedShaders = new ArrayList<>();

  public ShaderProgram() {
    programId = GL20.glCreateProgram();
  }

  public void attachShader(Shader shader) {
    GL20.glAttachShader(programId, shader.shaderId);
    attachedShaders.add(shader);
  }

  @Override
  protected void finalize() {
    // TODO: no OpenGL context here, find alternative
    // attachedShaders.forEach((shader) -> GL20.glDetachShader(programId, shader.shaderId));
    // GL20.glDeleteProgram(programId);
  }
}
