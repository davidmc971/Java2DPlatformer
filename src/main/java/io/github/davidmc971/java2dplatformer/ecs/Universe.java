package io.github.davidmc971.java2dplatformer.ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Universe {
    private long nextEntityId = 1;
    private List<Entity> entities = new ArrayList<>();
    private Map<String, List<Component>> components = new HashMap<>();
    private Map<Entity, Set<Component>> entityComponentMapping = new HashMap<>();

    public Entity createEntity() {
        assert nextEntityId < Long.MAX_VALUE : "Maximum number of entities reached.";
        Entity newEntity = new Entity(nextEntityId++);
        entities.add(newEntity);
        entityComponentMapping.put(newEntity, new HashSet<>());
        return newEntity;
    }

    public Set<Component> getComponents(Entity entity) {
        return entityComponentMapping.get(entity);
    }

    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        // TODO: safety
        return (List<T>) components.get(componentClass.getName());
    }

    public void addComponentToEntity(Component component, Entity entity) {
        
        entityComponentMapping.get(entity).add(component);
    }

    public void removeComponentFromEntity(Component component, Entity entity) {
        entityComponentMapping.get(entity).remove(component);
    }


}
