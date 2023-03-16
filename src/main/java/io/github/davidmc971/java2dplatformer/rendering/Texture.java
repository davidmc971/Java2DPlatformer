package io.github.davidmc971.java2dplatformer.rendering;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import io.github.davidmc971.java2dplatformer.main.Disposable;
import io.github.davidmc971.java2dplatformer.util.GLTextureSlot;

public class Texture implements Disposable {
  public final int textureId;
  public final IntBuffer width, height, channels;
  public final ByteBuffer image;

  public Texture(ByteBuffer image, IntBuffer width, IntBuffer height, IntBuffer channels) {
    if (image == null) {
      assert false : "Error: Image not loaded @" + this.getClass().getName();
    }

    textureId = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

    this.image = image;
    this.width = width;
    this.height = height;
    this.channels = channels;

    System.out.println("Creating Texture instance with width and height of "
        + width.get(0) + "x" + height.get(0)
        + " and " + channels.get(0) + " color channels.");

    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,
        width.get(0), height.get(0), 0, GL11.GL_RGBA,
        GL11.GL_UNSIGNED_BYTE, image);

  }

  public static Texture loadFromFilepath(String filepath) {
    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    ByteBuffer image = org.lwjgl.stb.STBImage.stbi_load(filepath, width, height, channels, 0);
    return new Texture(image, width, height, channels);
  }

  public static Texture loadInternal(String internalPath) {
    byte[] rawImageData;
    try {
      rawImageData = Texture.class.getResourceAsStream(internalPath).readAllBytes();
      System.out.println("Loaded image with " + rawImageData.length + " bytes.");
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    IntBuffer width = BufferUtils.createIntBuffer(1);
    IntBuffer height = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);
    ByteBuffer rawImageDataBuffer = BufferUtils.createByteBuffer(rawImageData.length);
    rawImageDataBuffer.put(rawImageData).flip();
    ByteBuffer image = org.lwjgl.stb.STBImage.stbi_load_from_memory(rawImageDataBuffer, width, height, channels, 0);
    return new Texture(image, width, height, channels);
  }

  public void bind(int slotNumber) {
    GL13.glActiveTexture(GLTextureSlot.get(slotNumber).glTextureSlot);
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
  }

  public void bindToTextureUnit45(int slot) {
    GL45.glBindTextureUnit(GLTextureSlot.get(slot).slotNumber, textureId);
  }

  public void unbind() {
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  }

  public static int getMaxTextureSlots() {
    return GLTextureSlot.getMaxTextureSlots();
  }

  public static int getMaxTextureSize() {
    return GL11.glGetInteger(GL13.GL_MAX_TEXTURE_SIZE);
  }

  public static int getMaxTextureArrayLayers() {
    return GL11.glGetInteger(GL30.GL_MAX_ARRAY_TEXTURE_LAYERS);
  }

  @Override
  public void dispose() {
    GL11.glDeleteTextures(textureId);
    // TODO: free immediately afterwards?
    org.lwjgl.stb.STBImage.stbi_image_free(image);
  }
}
