package io.github.davidmc971.java2dplatformer.framework;

import java.awt.Rectangle;
import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector4f;

import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public abstract class GameObject {
	protected Vector3f position = new Vector3f();
	protected Vector3f lastPosition = new Vector3f();
	protected Vector3f interpolatedPosition = new Vector3f();
	protected Vector3f dimensions = new Vector3f(32, 32, 0);
	protected Vector3f velocity = new Vector3f();
	protected Vector4f color = new Vector4f(1, 1, 1, 1);
	private boolean castsShadow = false;

	protected boolean falling = true, jumping = false;

	protected ObjectId id;

	public GameObject(float x, float y, ObjectId id) {
		position.set(x, y, 0);
		interpolatedPosition.set(position);
		lastPosition.set(position);
		this.id = id;
	}

	public void preUpdate() {
		lastPosition.set(position);
	}

	public abstract void update(float dt, List<GameObject> object);

	public void onRender(Renderer renderer, float lerp) {
		preRender(lerp);
		render(renderer);
	}

	public void preRender(float lerp) {
		if (position.equals(lastPosition))
			return;
		lastPosition.lerp(position, lerp, interpolatedPosition);
	}

	public abstract void render(Renderer renderer);

	public abstract Rectangle getBounds();

	public float getX() {
		return position.x;
	}

	public void setX(float x) {
		position.x = x;
	}

	public float getY() {
		return position.y;
	}

	public void setY(float y) {
		position.y = y;
	}

	public float getVelX() {
		return velocity.x;
	}

	public void setVelX(float velX) {
		velocity.x = velX;
	}

	public float getVelY() {
		return velocity.y;
	}

	public void setVelY(float velY) {
		velocity.y = velY;
	}

	public float getWidth() {
		return dimensions.x;
	}

	public void setWidth(float width) {
		dimensions.x = width;
	}

	public float getHeight() {
		return dimensions.y;
	}

	public void setHeight(float height) {
		dimensions.y = height;
	}

	public ObjectId getId() {
		return id;
	}

	public boolean isFalling() {
		return falling;
	}

	public void setFalling(boolean falling) {
		this.falling = falling;
	}

	public boolean isJumping() {
		return jumping;
	}

	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getLastPosition() {
		return lastPosition;
	}

	public Vector3f getInterpolatedPosition() {
		return interpolatedPosition;
	}

	public Vector3f getDimensions() {
		return dimensions;
	}

	public Vector3f getVelocity() {
		return velocity;
	}

	public Vector4f getColor() {
		return color;
	}

	public boolean isCastsShadow() {
		return castsShadow;
	}

	public void setCastsShadow(boolean castsShadow) {
		this.castsShadow = castsShadow;
	}
}
