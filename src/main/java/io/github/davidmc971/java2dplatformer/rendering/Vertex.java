package io.github.davidmc971.java2dplatformer.rendering;

// import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Vertex {
	public Vector3f position;
	public Vector4f color;
	// public Vector2f uvCoords;
	// float texId;
	public static int size() {
		return Float.SIZE * 7;
	}
}
