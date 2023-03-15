package io.github.davidmc971.java2dplatformer.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.davidmc971.java2dplatformer.rendering.Shader;
import io.github.davidmc971.java2dplatformer.rendering.Texture;
import io.github.davidmc971.java2dplatformer.rendering.ShaderType.CouldNotInferShaderTypeException;

public class AssetManager {
  private static Map<String, Shader> shaderPool = new HashMap<>();
  private static Map<String, Texture> texturePool = new HashMap<>();

  private static final String PREFIX_EXTERNAL = "@External:";
  private static final String PREFIX_INTERNAL = "@Internal:";

  public static Shader getShaderExternal(String assetPath)
      throws FileNotFoundException, IOException, CouldNotInferShaderTypeException {
    Shader asset = shaderPool.get(PREFIX_EXTERNAL + assetPath);
    if (asset != null) {
      return asset;
    }
    asset = Shader.loadExternal(assetPath);
    shaderPool.put(PREFIX_EXTERNAL + assetPath, asset);
    return asset;
  }

  public static Shader getShaderInternal(String assetPath)
      throws IOException, CouldNotInferShaderTypeException {
    Shader asset = shaderPool.get(PREFIX_INTERNAL + assetPath);
    if (asset != null) {
      return asset;
    }
    asset = Shader.loadInternal(assetPath);
    shaderPool.put(PREFIX_INTERNAL + assetPath, asset);
    return asset;
  }

  public static Texture getTextureExternal(String assetPath) {
    Texture asset = texturePool.get(PREFIX_EXTERNAL + assetPath);
    if (asset != null) {
      return asset;
    }
    asset = Texture.loadFromFilepath(assetPath);
    texturePool.put(PREFIX_EXTERNAL + assetPath, asset);
    return asset;
  }

  public static Texture getTextureInternal(String assetPath) {
    Texture asset = texturePool.get(PREFIX_INTERNAL + assetPath);
    if (asset != null) {
      return asset;
    }
    asset = Texture.loadInternal(assetPath);
    texturePool.put(PREFIX_INTERNAL + assetPath, asset);
    return asset;
  }

}
