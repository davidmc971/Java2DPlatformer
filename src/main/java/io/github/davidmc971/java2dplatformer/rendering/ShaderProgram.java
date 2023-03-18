package io.github.davidmc971.java2dplatformer.rendering;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram {
  public final int programId;
  private List<Shader> attachedShaders = new ArrayList<>();
  private Map<String, Integer> uniforms = new HashMap<>();

  public ShaderProgram() {
    programId = GL20.glCreateProgram();
  }

  public void attachShader(Shader shader) {
    GL20.glAttachShader(programId, shader.shaderId);
    attachedShaders.add(shader);
  }

  public void link() {
    GL20.glLinkProgram(programId);
    if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
      System.out.println(GL20.glGetProgramInfoLog(programId, GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH)));
      throw new RuntimeException();
    }
  }

  public int uniform(String uniformName) {
    return uniforms.computeIfAbsent(uniformName, (_uniformName) -> {
      int location = GL20.glGetUniformLocation(programId, _uniformName);
      if (location == -1) {
        System.err.println("Warning, shader " + programId + " does not contain uniform \"" + _uniformName + "\".");
        return -1;
      }
      return location;
    });
  }

  public void sendUniformMatrix4f(String uniformName, FloatBuffer matrix4fBuffer) {
    int location = uniform(uniformName);
    if (location == -1)
      return;
    GL20.glUniformMatrix4fv(location, false, matrix4fBuffer);
  }

  public void sendTextureUniform(String uniformName, int[] textureSlots) {
    use();
    GL20.glUniform1iv(GL20.glGetUniformLocation(programId, uniformName), textureSlots);
  }

  public void use() {
    GL20.glUseProgram(programId);
  }

  @Override
  protected void finalize() {
    // TODO: no OpenGL context here, find alternative
    // attachedShaders.forEach((shader) -> GL20.glDetachShader(programId,
    // shader.shaderId));
    // GL20.glDeleteProgram(programId);
  }
}
