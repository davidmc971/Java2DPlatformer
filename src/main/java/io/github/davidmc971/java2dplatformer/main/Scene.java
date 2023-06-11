package io.github.davidmc971.java2dplatformer.main;

import java.util.ArrayList;
import java.util.List;

import io.github.davidmc971.java2dplatformer.ecs.Entity;

public abstract class Scene {
  protected Camera camera;
  private boolean isRunning = false;
  protected List<Entity> gameObjects = new ArrayList<>();

  public Scene() {

  }

  public void init() {

  }

  public void start() {
    gameObjects.forEach((go) -> go.start());
    isRunning = true;
  }

  public void addGameObject(Entity gameObject) {
    gameObjects.add(gameObject);
    if (isRunning) {
      gameObject.start();
    }
  }

  public abstract void update(float t, float dt);
}
