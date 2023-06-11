package io.github.davidmc971.java2dplatformer.ecs;

import java.util.ArrayList;
import java.util.List;

public class Entity {
  private static long ENTITY_ID_COUNTER = 0;

  private long entityId = ENTITY_ID_COUNTER++;

  private List<Component> components = new ArrayList<>();

  public <T extends Component> T getComponent(Class<T> componentClass) {
    for (Component c : components) {
      if (componentClass.isAssignableFrom(c.getClass())) {
        try {
          return componentClass.cast(c);
        } catch (ClassCastException e) {
          e.printStackTrace();
          assert false : "Error: Casting component.";
        }
      }
    }
    return null;
  }

  public List<Component> getComponents() {
    return components;
  }

  public <T extends Component> void removeComponent(Class<T> componentClass) {
    for (int i = 0; i < components.size(); i++) {
      Component c = components.get(i);
      if (componentClass.isAssignableFrom(c.getClass())) {
        components.remove(i);
        return;
      }
    }
  }

  public Entity addComponent(Component c) {
    components.add(c);
    c.gameObject = this;
    return this;
  }

  public void update(float t, float dt) {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).update(t, dt);
    }
  }

  public void start() {
    for (int i = 0; i < components.size(); i++) {
      components.get(i).start();
    }
  }

  public long getEntityId() {
    return entityId;
  }
}
