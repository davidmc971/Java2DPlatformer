package io.github.davidmc971.java2dplatformer.main;

import java.util.LinkedList;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.objects.Player;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Handler {

	public LinkedList<GameObject> object = new LinkedList<GameObject>();

	private GameObject tempObject;
	private Game game;

	public Handler(Game game) {
		this.game = game;
	}

	public void tick(float t, float dt) {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);
			tempObject.preUpdate();
			tempObject.update(dt, object);
		}
	}

	public void render(Renderer renderer, float lerp, Camera camera) {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() != ObjectId.Background) {
				if (tempObject.getId() != ObjectId.Player) {
					tempObject.onRender(renderer, lerp);
				}
			}
		}
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Player) {
				tempObject.preRender(lerp);
				camera.tick((Player) tempObject);
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

	public void renderBG(Renderer renderer, float lerp) {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Background) {
				((BGBlock) tempObject).onRender(renderer, lerp);
			}
		}
	}

	public LevelHandler getLevelHandler() {
		return game.getLevelHandler();
	}
}
