package io.github.davidmc971.java2dplatformer.rendering;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
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

  public void link() {
    GL20.glLinkProgram(programId);
		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) != GL11.GL_TRUE) {
			System.out.println(GL20.glGetProgramInfoLog(programId, GL20.glGetProgrami(programId, GL20.GL_INFO_LOG_LENGTH)));
			throw new RuntimeException();
		}
  }

  public int getUniformLocation(String uniformName) {
    int location = GL20.glGetUniformLocation(programId, uniformName);
    if (location == -1) {
      throw new RuntimeException();
    }
    return location;
  }

  @Override
  protected void finalize() {
    // TODO: no OpenGL context here, find alternative
    // attachedShaders.forEach((shader) -> GL20.glDetachShader(programId, shader.shaderId));
    // GL20.glDeleteProgram(programId);
  }
}
