package io.github.davidmc971.java2dplatformer.objects;

import java.awt.Rectangle;

import java.util.LinkedList;

import org.joml.Vector3f;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.main.Game;
import io.github.davidmc971.java2dplatformer.main.Handler;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Player extends GameObject {

	private float width = 24, height = 48;
	private float gravity = 700f;
	private final float MAX_SPEED = 600;
	private Handler handler;
	private float checkX, checkY;
	private boolean cameraFocus = true;

	public Player(float x, float y, Handler handler, ObjectId id) {
		super(x, y, id);
		this.dimensions.set(width, height, 0);
		this.handler = handler;
		this.checkX = getX();
		this.checkY = getY();
		if (handler.getLevelHandler().getLevel() == 2) {
			this.gravity *= 0.5f;
		}
	}

	public void update(float dt, LinkedList<GameObject> object) {
		position.x += velocity.x * dt;
		position.y += velocity.y * dt;
		if (falling || jumping) {
			velocity.y += gravity * dt;
			if (velocity.y >= 0) {
				velocity.y *= 1 + (0.15f * dt);
			}
			if (velocity.y != 0 && Math.abs(velocity.y) > MAX_SPEED) {
				velocity.y = MAX_SPEED * (velocity.y / Math.abs(velocity.y));
			}
		}
		collision(dt, object);
	}

	private void collision(float dt, LinkedList<GameObject> object) {
		for (int i = 0; i < handler.objects.size(); i++) {
			GameObject tempObject = handler.objects.get(i);
			if (tempObject.getId() == ObjectId.Block) {
				if (getBoundsAll()[0].intersects(tempObject.getBounds())) {
					position.y = tempObject.getY() - dimensions.y;
					velocity.y = 0;
					falling = false;
					jumping = false;
					position.y = (int) position.y;
				} else {
					falling = true;
				}
				if (getBoundsAll()[1].intersects(tempObject.getBounds())) {
					position.y = tempObject.getY() + tempObject.getHeight();
					velocity.y = 0;
				}
				if (getBoundsAll()[2].intersects(tempObject.getBounds())) {
					position.x = tempObject.getX() - dimensions.x;
				}
				if (getBoundsAll()[3].intersects(tempObject.getBounds())) {
					position.x = tempObject.getX() + tempObject.getWidth();
				}
			}
			if (tempObject.getId() == ObjectId.Death) {
				if (getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds())) {
					this.setX(this.getCheckX());
					this.setY(this.getCheckY());
				}

			}
			if (tempObject.getId() == ObjectId.Check) {
				if (getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds())) {
					this.setCheckX(tempObject.getX() + 1);
					this.setCheckY(tempObject.getY() + 1);
				}
			}
			if (tempObject.getId() == ObjectId.Finish) {
				if (getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds())) {
					if (handler.getLevelHandler().isActive() && !handler.getLevelHandler().isLoading()) {
						System.out.println("Starting next level!");
						handler.getLevelHandler().nextLevel();
					}
				}

			}
			if (tempObject.getId() == ObjectId.Elevator) {
				if (getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds())) {
					this.setVelY(this.getVelY() - 450f * dt);
				}
			}
		}
	}

	public void render(Renderer renderer) {
		renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, dimensions.x,
				dimensions.y,
				200f / 255f, 100f / 255f, 0f / 255f, 127);
		if (Game.DEBUG) {
			Rectangle[] bounds = getBoundsAll(interpolatedPosition);
			for (int i = 0; i < bounds.length; i++) {
				Rectangle r = bounds[i];
				renderer.drawQuad(r.x, r.y, 0, r.width, r.height,
						(float) (255 / 4 * (i + 1)) / 255f, 255f / 255f, (float) (255 / (i + 1)) / 255f, 1);
			}
			renderer.drawQuad(bounds[0].x, bounds[0].y, 0, bounds[0].width, bounds[0].height, 1, 1, 1, 1);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, (int) width, (int) height);
	}

	public Rectangle[] getBoundsAll() {
		return getBoundsAll(position);
	}

	public Rectangle[] getBoundsAll(Vector3f position) {
		return new Rectangle[] {
				new Rectangle((int) (position.x + width / 4), (int) (position.y + height / 2), (int) width / 2,
						(int) height / 2 + 1), // Bottom
				new Rectangle((int) (position.x + width / 4), (int) position.y, (int) width / 2, (int) height / 2), // Top
				new Rectangle((int) (position.x + width - width / 4), (int) (position.y + 4), (int) width / 4,
						(int) (height - 8)), // Right
				new Rectangle((int) position.x, (int) (position.y + 4), (int) width / 4, (int) (height - 8)) };// Left
	}

	public float getCheckX() {
		return checkX;
	}

	public void setCheckX(float checkX) {
		this.checkX = checkX;
	}

	public float getCheckY() {
		return checkY;
	}

	public void setCheckY(float checkY) {
		this.checkY = checkY;
	}

	public boolean getFocusCamera() {
		return cameraFocus;
	}

	public void setCameraFocus(boolean focus) {
		this.cameraFocus = focus;
	}
}
