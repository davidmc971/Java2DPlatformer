package io.github.davidmc971.java2dplatformer.main;

import java.util.LinkedList;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.objects.Player;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Handler {

	public LinkedList<GameObject> objects = new LinkedList<GameObject>();

	private Game game;

	public Handler(Game game) {
		this.game = game;
	}

	public void tick(float t, float dt) {
		if (!game.getLevelHandler().isActive() || game.getLevelHandler().isLoading()) {
			return;
		}

		for (GameObject tempObject : objects) {
			tempObject.preUpdate();
			tempObject.update(dt, objects);
		}
	}

	private Player playerReference = null;

	public void render(Renderer renderer, float lerp, Camera camera) {

		for (GameObject tempObject : objects) {
			switch (tempObject.getId()) {
				case Background:
					break;
				case Player:
					playerReference = (Player) tempObject;
					break;
				default:
					tempObject.onRender(renderer, lerp);
			}
		}

		if (playerReference != null) {
			playerReference.preRender(lerp);
			camera.tick(playerReference);
			playerReference.render(renderer);
		}
	}

	public void addObject(GameObject object) {
		this.objects.add(object);
	}

	public void removeObject(GameObject object) {
		this.objects.remove(object);
	}

	public void renderBG(Renderer renderer, float lerp) {
		for (GameObject tempObject : objects) {
			if (tempObject.getId() == ObjectId.Background) {
				((BGBlock) tempObject).onRender(renderer, lerp);
			}
		}
	}

	public LevelHandler getLevelHandler() {
		return game.getLevelHandler();
	}
}
