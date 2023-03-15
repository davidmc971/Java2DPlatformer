package io.github.davidmc971.java2dplatformer.main;

import java.util.LinkedList;

import com.badlogic.ashley.core.PooledEngine;

import io.github.davidmc971.java2dplatformer.ecs.ECSEngine;
import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.objects.Player;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Handler {

	public LinkedList<GameObject> objects = new LinkedList<GameObject>();

	private GameObject tempObject;
	private Game game;
	private ECSEngine ecsEngine;

	public Handler(Game game, ECSEngine ecsEngine, PooledEngine ashleyECSPooledEngine) {
		this.game = game;
		this.ecsEngine = ecsEngine;
	}

	public void tick(float t, float dt) {
		if (!game.getLevelHandler().isActive() || game.getLevelHandler().isLoading()) {
			tempObject = null;
			return;
		}
		for (int i = 0; i < objects.size(); i++) {
			tempObject = objects.get(i);
			tempObject.preUpdate();
			tempObject.update(dt, objects);
		}
	}

	public void render(Renderer renderer, float lerp, Camera camera) {

		for (int i = 0; i < objects.size(); i++) {
			tempObject = objects.get(i);

			if (tempObject.getId() != ObjectId.Background) {
				if (tempObject.getId() != ObjectId.Player) {
					tempObject.onRender(renderer, lerp);
				}
			}
		}
		for (int i = 0; i < objects.size(); i++) {
			tempObject = objects.get(i);

			if (tempObject.getId() == ObjectId.Player) {
				tempObject.preRender(lerp);
				camera.tick((Player) tempObject);
				tempObject.render(renderer);
			}
		}
	}

	public void addObject(GameObject object) {
		this.objects.add(object);
	}

	public void removeObject(GameObject object) {
		this.objects.remove(object);
	}

	public void renderBG(Renderer renderer, float lerp) {
		for (int i = 0; i < objects.size(); i++) {
			tempObject = objects.get(i);

			if (tempObject.getId() == ObjectId.Background) {
				((BGBlock) tempObject).onRender(renderer, lerp);
			}
		}
	}

	public LevelHandler getLevelHandler() {
		return game.getLevelHandler();
	}
}
