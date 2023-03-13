package io.github.davidmc971.java2dplatformer.objects;

import java.awt.Rectangle;
import java.util.LinkedList;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class BGBlock extends GameObject {
	private float width = 32, height = 32;
	private float counter = 0;

	public BGBlock(float x, float y, int r, int g, int b) {
		super(x, y, ObjectId.Background);
	}

	public void update(float dt, LinkedList<GameObject> object) {
		counter += 0.1;
		if (counter == 360) {
			counter = 0;
		}
	}

	public void render(Renderer renderer) {
		int a = (int) Math.abs(Math.sin(counter) * 255);
		a *= 0.05;
		int z = 8;
		color.set((color.x+a)/z, (color.y+a)/z, (color.z+a)/z);
		// glRectf(x, y, x+width, y+height);
		renderer.render(this);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) position.x, (int) position.y, (int) width, (int) height);
	}

}
