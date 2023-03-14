package io.github.davidmc971.java2dplatformer.main;

import java.util.ArrayList;
import java.util.List;

import io.github.davidmc971.java2dplatformer.ecs.GameObject;

public abstract class Scene {
  protected Camera camera;
  private boolean isRunning = false;
  protected List<GameObject> gameObjects = new ArrayList<>();

  public Scene() {

  }

  public void init() {

  }

  public void start() {
    gameObjects.forEach((go) -> go.start());
    isRunning = true;
  }

  public void addGameObject(GameObject gameObject) {
    gameObjects.add(gameObject);
    if (isRunning) {
      gameObject.start();
    }
  }

  public abstract void update(float t, float dt);
}
