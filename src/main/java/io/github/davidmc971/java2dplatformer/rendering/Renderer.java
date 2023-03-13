package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import io.github.davidmc971.java2dplatformer.main.Camera;
import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class Renderer {

  private ShaderProgram shaderProgram;

  private int uLocModel, uLocView, uLocProjection, vao, vbo, ebo;

  private FloatBuffer vertexBuffer;
  private IntBuffer elementBuffer;

  private FloatBuffer fbProjectionMatrix;
  private FloatBuffer fbViewMatrix;
  private FloatBuffer fbModelMatrix;

  private Camera camera;
  private Matrix4f m4fModel = new Matrix4f();

  private static final int RENDER_BATCH_QUAD_AMOUNT = 2048;

  public void initialize(Camera camera) {
    this.camera = camera;
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

    vertexBuffer = BufferUtils.createFloatBuffer(7 * 4 * RENDER_BATCH_QUAD_AMOUNT);
    elementBuffer = BufferUtils.createIntBuffer(6 * RENDER_BATCH_QUAD_AMOUNT);

    vbo = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_DYNAMIC_DRAW);

    ebo = GL15.glGenBuffers();
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL15.GL_DYNAMIC_DRAW);

    int positionsSize = 3;
    int colorSize = 4;
    int vertexSizeBytes = (positionsSize + colorSize) * Float.BYTES;
    GL20.glVertexAttribPointer(0, positionsSize, GL11.GL_FLOAT, false, vertexSizeBytes, 0);
    GL20.glEnableVertexAttribArray(0);

    GL20.glVertexAttribPointer(1, colorSize, GL11.GL_FLOAT, false, vertexSizeBytes, positionsSize * Float.BYTES);
    GL20.glEnableVertexAttribArray(1);

    fbProjectionMatrix = MemoryUtil.memAllocFloat(16);
    fbViewMatrix = MemoryUtil.memAllocFloat(16);
    fbModelMatrix = MemoryUtil.memAllocFloat(16);
  }

  public void preFrame() {
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    GL20.glUseProgram(shaderProgram.programId);
    GL30.glBindVertexArray(vao);

    GL20.glEnableVertexAttribArray(0);
    GL20.glEnableVertexAttribArray(1);

    m4fModel.identity();
    GL20.glUniformMatrix4fv(uLocProjection, false, camera.getProjectionMatrix().get(fbProjectionMatrix));
    GL20.glUniformMatrix4fv(uLocView, false, camera.getViewMatrix().get(fbViewMatrix));
    GL20.glUniformMatrix4fv(uLocModel, false, m4fModel.get(fbModelMatrix));
  }

  public void postFrame() {
    GL20.glDisableVertexAttribArray(0);
    GL20.glDisableVertexAttribArray(1);

    GL30.glBindVertexArray(0);
    GL20.glUseProgram(0);
  }

  public void render(io.github.davidmc971.java2dplatformer.ecs.GameObject gameObject) {

  }

  private int batchElementOffset = 0;
  private static Vector4f colorWhite = new Vector4f(1, 1, 1, 1);

  public void render(io.github.davidmc971.java2dplatformer.framework.GameObject gameObject) {
    drawQuad(gameObject.getX(), gameObject.getY(), 0, gameObject.getWidth(), gameObject.getHeight(), colorWhite);
  }

  public void drawQuad(Vector3f position, Vector3f dimensions, Vector4f color) {
    drawQuad(position.x, position.y, position.z, dimensions.x, dimensions.y, color);
  }

  public void drawQuad(float x, float y, float z, float w, float h, Vector4f color) {
    drawQuad(x, y, z, w, h, color.x, color.y, color.z, color.w);
  }

  public void drawQuad(float x, float y, float z, float w, float h, float r, float g, float b, float a) {
    if (vertexBuffer.remaining() < 4 || elementBuffer.remaining() < 6)
      flush();

    vertexBuffer.put(x + w).put(y).put(z)
        .put(r).put(g).put(b).put(a);
    vertexBuffer.put(x).put(y + h).put(z)
        .put(r).put(g).put(b).put(a);
    vertexBuffer.put(x + w).put(y + h).put(z)
        .put(r).put(g).put(b).put(a);
    vertexBuffer.put(x).put(y).put(z)
        .put(r).put(g).put(b).put(a);

    elementBuffer.put(2 + batchElementOffset).put(1 + batchElementOffset).put(0 + batchElementOffset);
    elementBuffer.put(0 + batchElementOffset).put(1 + batchElementOffset).put(3 + batchElementOffset);
    batchElementOffset += 4;
  }

  private float[] vertexArray = {
      // position // color // uv
      740, 260, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 1, 0, // 0
      540, 460, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // 0, 1, // 1
      740, 460, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, // 1, 1, // 2
      540, 260, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f // , 1, 0 // 3
  };

  // counterclockwise
  private int[] elementArray = {
      2, 1, 0,
      0, 1, 3
  };

  private int[] elementArrayWithOffset = new int[elementArray.length];

  public void queueTestSquare() {
    vertexBuffer.put(vertexArray);
    for (int i = 0; i < elementArray.length; i++) {
      elementArrayWithOffset[i] = elementArray[i] + batchElementOffset;
    }
    elementBuffer.put(elementArray);
    batchElementOffset += 4;
  }

  private int currentBatchSize;

  public void flush() {
    currentBatchSize = elementBuffer.position();

    vertexBuffer.flip();
    elementBuffer.flip();

    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexBuffer);

    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
    GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, elementBuffer);

    GL11.glDrawElements(GL11.GL_TRIANGLES, currentBatchSize, GL11.GL_UNSIGNED_INT, 0);

    vertexBuffer.clear();
    elementBuffer.clear();
    batchElementOffset = 0;
  }

  @Override
  protected void finalize() {
    MemoryUtil.memFree(fbProjectionMatrix);
    MemoryUtil.memFree(fbViewMatrix);
    MemoryUtil.memFree(fbModelMatrix);
  }
}