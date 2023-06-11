package io.github.davidmc971.java2dplatformer.ecs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.github.davidmc971.java2dplatformer.main.Updatable;

public class ECSEngine implements Updatable {
  private List<Entity> entities = new ArrayList<>();

  private HashMap<Class<? extends Component>, List<Component>> componentPool = new HashMap<>();
  private List<Component> lastCheckedComponentList = null;

  private List<EntitySystem> entitySystems = new ArrayList<>();

  public void addEntity(Entity entityGameObject) {
    entityGameObject.getComponents().forEach((c) -> {
      lastCheckedComponentList = componentPool.get(c.getClass());
      if (lastCheckedComponentList == null) {
        lastCheckedComponentList = new ArrayList<Component>();
        componentPool.put(c.getClass(), lastCheckedComponentList);
      }
      lastCheckedComponentList.add(c);
    });
    entities.add(entityGameObject);
  }

  public void addEntitySystem(EntitySystem entitySystem) {
    entitySystem.setECSEngine(this);
    entitySystems.add(entitySystem);
    entitySystems.sort(Comparator.naturalOrder());
  }

  public <T extends Component> List<Component> getComponents(Class<T> componentClass) {
    return componentPool.get(componentClass);
  }

  @Override
  public void update(float t, float dt) {
    entitySystems.forEach((es) -> {
      es.update(t, dt);
    });
  }

}
