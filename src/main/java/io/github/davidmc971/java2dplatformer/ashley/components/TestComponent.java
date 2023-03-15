package io.github.davidmc971.java2dplatformer.ashley.components;

import org.joml.Vector3f;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.utils.Pool.Poolable;

public class TestComponent implements Component, Poolable {
    public static final ComponentMapper<TestComponent> componentMapper = ComponentMapper.getFor(TestComponent.class);

    public Vector3f position = new Vector3f();

    @Override
    public void reset() {
        position.set(0);
    }
    
}
