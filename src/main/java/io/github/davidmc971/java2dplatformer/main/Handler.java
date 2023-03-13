package io.github.davidmc971.java2dplatformer.main;

import java.util.LinkedList;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Handler {

	public LinkedList<GameObject> object = new LinkedList<GameObject>();

	private GameObject tempObject;
	private Game game;

	public Handler(Game game) {
		this.game = game;
	}

	public void tick() {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			tempObject.tick(object);
		}

	}

	public void render(Renderer renderer) {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() != ObjectId.Background) {
				if (tempObject.getId() != ObjectId.Player) {
					tempObject.render(renderer);
				}
			}
		}
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Player) {
				tempObject.render(renderer);
			}
		}
	}

	public void addObject(GameObject object) {
		this.object.add(object);
	}

	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void renderBG(Renderer renderer) {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Background) {
				((BGBlock) tempObject).render(renderer);
			}
		}
	}

	public LevelHandler getLevelHandler() {
		return game.getLevelHandler();
	}
}
