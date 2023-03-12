package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class Renderer {

  private ShaderProgram shaderProgram;

  private int uLocModel, uLocView, uLocProjection, vao, vbo, ebo;

  private FloatBuffer vertexBuffer;
  private IntBuffer elementBuffer;

  private static final int RENDER_BATCH_QUAD_AMOUNT = 2048;

  public void initialize() {
    shaderProgram = new ShaderProgram();
    try {
      shaderProgram.attachShader(Shader.loadInternal("/shaders/main.vert"));
      shaderProgram.attachShader(Shader.loadInternal("/shaders/main.frag"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (CouldNotInferShaderTypeException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    shaderProgram.link();

    uLocModel = shaderProgram.getUniformLocation("model");
    uLocView = shaderProgram.getUniformLocation("view");
    uLocProjection = shaderProgram.getUniformLocation("projection");

    vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);

    vertexBuffer = BufferUtils.createFloatBuffer(Vertex.size() * 4 * RENDER_BATCH_QUAD_AMOUNT);
    elementBuffer = BufferUtils.createIntBuffer(Integer.SIZE * 6 * RENDER_BATCH_QUAD_AMOUNT);

    vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STREAM_DRAW);

    ebo = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_STREAM_DRAW);

    int positionsSize = 3;
		int colorSize = 4;
		int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;
		GL20.glVertexAttribPointer(0, positionsSize, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
		GL20.glEnableVertexAttribArray(0);

		GL20.glVertexAttribPointer(1, colorSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
		GL20.glEnableVertexAttribArray(1);
  }

  public void render(io.github.davidmc971.java2dplatformer.ecs.GameObject gameObject) {

  }

  public void render(io.github.davidmc971.java2dplatformer.framework.GameObject gameObject) {

  }

  public void flush() {
    
  }
}
