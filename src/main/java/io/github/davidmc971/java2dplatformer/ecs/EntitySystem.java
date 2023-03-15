package io.github.davidmc971.java2dplatformer.ecs;

import io.github.davidmc971.java2dplatformer.main.Initializable;
import io.github.davidmc971.java2dplatformer.main.Updatable;

public abstract class EntitySystem implements Comparable<EntitySystem>, Initializable, Updatable {
  protected int priority;
  protected ECSEngine ecsEngine;

  public int getPriority() {
    return priority;
  }

  @Override
  public int compareTo(EntitySystem otherEntitySystem) {
    return otherEntitySystem.getPriority() - priority;
  }

  public void setECSEngine(ECSEngine ecsEngine) {
    this.ecsEngine = ecsEngine;
  }
}
