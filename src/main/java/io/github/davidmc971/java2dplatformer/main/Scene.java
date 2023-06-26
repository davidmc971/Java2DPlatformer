package io.github.davidmc971.java2dplatformer.main;

public abstract class Scene {
  protected Camera camera;
  private boolean isRunning = false;

  public Scene() {

  }

  public void init() {
    
  }

  public void start() {
    // gameObjects.forEach((go) -> go.start());
    isRunning = true;
  }

  public abstract void update(float t, float dt);
}
