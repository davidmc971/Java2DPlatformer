package io.github.davidmc971.java2dplatformer.rendering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class Shader {
  public final int shaderId;
  public final ShaderType shaderType;

  public Shader(ShaderType shaderType, CharSequence shaderSource) {
    this.shaderType = shaderType;
    shaderId = GL20.glCreateShader(shaderType.glShaderTypeId);
    GL20.glShaderSource(shaderId, shaderSource);
    GL20.glCompileShader(shaderId);

    if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) != GL11.GL_TRUE) {
      System.err.println(GL20.glGetShaderInfoLog(shaderId, GL20.glGetShaderi(shaderId, GL20.GL_INFO_LOG_LENGTH)));
      throw new RuntimeException();
    }
  }

  public static Shader loadInternal(String resourcePath) throws IOException, CouldNotInferShaderTypeException {
    return loadInternal(ShaderType.inferFromPath(resourcePath), resourcePath);
  }

  public static Shader loadInternal(ShaderType shaderType, String resourcePath) throws IOException {
    return loadFromStream(shaderType, Shader.class.getResourceAsStream(resourcePath));
  }

  public static Shader loadExternal(String filePath)
      throws FileNotFoundException, IOException, CouldNotInferShaderTypeException {
    return loadExternal(ShaderType.inferFromPath(filePath), filePath);
  }

  public static Shader loadExternal(ShaderType shaderType, String filePath) throws FileNotFoundException, IOException {
    return loadFromStream(shaderType, new FileInputStream(new File(filePath)));
  }

  public static Shader loadFromStream(ShaderType shaderType, InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder stringBuilder = new StringBuilder();
    String line = "";
    while ((line = reader.readLine()) != null) {
      stringBuilder.append(line).append("\n");
    }
    return new Shader(shaderType, stringBuilder.toString());
  }

  @Override
  protected void finalize() {
    // TODO: no OpenGL context here, find alternative
    // GL20.glDeleteShader(shaderId);
  }
}
