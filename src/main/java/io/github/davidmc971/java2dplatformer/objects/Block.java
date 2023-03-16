package io.github.davidmc971.java2dplatformer.objects;

import java.awt.Rectangle;
import java.util.List;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Block extends io.github.davidmc971.java2dplatformer.framework.GameObject {

	public Block(float x, float y, ObjectId id) {
		super(x, y, id);
	}

	public void update(float dt, List<GameObject> object) {

	}

	public void render(Renderer renderer) {
		if (this.getId() == ObjectId.Finish) {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 16, 16,
					1, 1, 1, 1);
			renderer.drawQuad(interpolatedPosition.x + 16, interpolatedPosition.y, interpolatedPosition.z, 16, 16,
					1, 1, 1, 1);
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y + 16, interpolatedPosition.z, 16, 16,
					20f / 255f, 20f / 255f, 20f / 255f, 1);
		} else if (this.getId() == ObjectId.Death) {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 32, 32,
					200f / 255f, 0, 0, 1);
		} else if (this.getId() == ObjectId.Check) {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 32, 32,
					0, 170f / 255f, 170f / 255f, 1);
		} else if (this.getId() == ObjectId.Elevator) {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 32, 32,
					20f / 255f, 20f / 255f, 170f / 255f, 1);
		} else if (this.getId() == ObjectId.Enemy) {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 32, 32,
					170f / 255f, 20f / 255f, 170f / 255f, 1);
		} else {
			renderer.drawQuad(interpolatedPosition.x, interpolatedPosition.y, interpolatedPosition.z, 32, 32,
					0f / 255f, 255f / 255f, 0f / 255f, 1);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, 32, 32);
	}
}
