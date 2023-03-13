package io.github.davidmc971.java2dplatformer.objects;

import java.awt.Rectangle;
import java.util.LinkedList;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Block extends io.github.davidmc971.java2dplatformer.framework.GameObject {

	public Block(float x, float y, ObjectId id) {
		super(x, y, id);
	}

	public void tick(LinkedList<GameObject> object) {

	}

	public void render(Renderer renderer) {
		if (this.getId() == ObjectId.Finish) {
			renderer.drawQuad(position.x, position.y, position.z, 16, 16,
					1, 1, 1, 1);
			renderer.drawQuad(position.x + 16, position.y, position.z, 16, 16,
					1, 1, 1, 1);
			renderer.drawQuad(position.x, position.y + 16, position.z, 16, 16,
					20f / 255f, 20f / 255f, 20f / 255f, 1);
		} else if (this.getId() == ObjectId.Death) {
			renderer.drawQuad(position.x, position.y, position.z, 32, 32,
					56f / 255f, 0, 0, 1);
			renderer.drawQuad(position.x + 2, position.y + 2, position.z, 28, 28,
					200f / 255f, 0, 0, 1);
		} else if (this.getId() == ObjectId.Check) {
			renderer.drawQuad(position.x, position.y, position.z, 32, 32,
					0, 10f / 255f, 10f / 255f, 1);
			renderer.drawQuad(position.x + 2, position.y + 2, position.z, 28, 28,
					0, 170f / 255f, 170f / 255f, 1);
		} else if (this.getId() == ObjectId.Elevator) {
			renderer.drawQuad(position.x, position.y, position.z, 32, 32,
					10f / 255f, 10f / 255f, 30f / 255f, 1);
			renderer.drawQuad(position.x + 2, position.y + 2, position.z, 28, 28,
					20f / 255f, 20f / 255f, 170f / 255f, 1);
		} else if (this.getId() == ObjectId.Enemy) {
			renderer.drawQuad(position.x, position.y, position.z, 32, 32,
					30f / 255f, 10f / 255f, 30f / 255f, 1);
			renderer.drawQuad(position.x + 2, position.y + 2, position.z, 28, 28,
					170f / 255f, 20f / 255f, 170f / 255f, 1);
		} else {
			renderer.drawQuad(position.x, position.y, position.z, 32, 32,
					0f / 255f, 40f / 255f, 0f / 255f, 1);
			renderer.drawQuad(position.x + 2, position.y + 2, position.z, 28, 28,
					0f / 255f, 255f / 255f, 0f / 255f, 1);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, 32, 32);
	}
}
