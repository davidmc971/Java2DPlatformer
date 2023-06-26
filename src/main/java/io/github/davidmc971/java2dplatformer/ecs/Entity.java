package io.github.davidmc971.java2dplatformer.ecs;

public class Entity {
    private long entityId;

    Entity(long entityId) {
        this.entityId = entityId;
    }

    /**
     * Convenience utility linking entity to universe, where you have to create entities.
     * @param universe The universe to create the entity in.
     * @return The resulting entity.
     */
    public static Entity createEntity(Universe universe) {
        return universe.createEntity();
    }

    public long getEntityId() {
        return entityId;
    }
}
