package io.github.davidmc971.java2dplatformer.ecs.components;

import org.joml.Vector4f;

import io.github.davidmc971.java2dplatformer.ecs.Component;

public class ColoredBoxRenderer extends Component {

  private Vector4f color;

  public ColoredBoxRenderer(Vector4f color) {
    this.color = color;
  }

  @Override
  public void start() {
    System.out.println("Component starting");
  }

  private boolean temp = false;

  @Override
  public void update(float t, float dt) {
    if (!temp) {
      System.out.println("Component updating");
      temp = true;
    }
  }

  public Vector4f getColor() {
    return this.color;
  }

}
