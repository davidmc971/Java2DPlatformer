package io.github.davidmc971.java2dplatformer.main;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.joml.Vector3f;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.objects.Player;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;

public class Handler {
	public static boolean USE_THREAD_POOL = false;
	public static int UPDATE_THREADS = 16;

	private ExecutorService executorService;

	public final List<GameObject> objects = new ArrayList<>();
	private boolean shouldLoadNextLevel = false;

	private Game game;

	public Handler(Game game) {
		this.game = game;

		if (USE_THREAD_POOL) {
			assert UPDATE_THREADS > 0
					: "Handler.UPDATE_THREADS <= 0 | Having less than one update thread is not possible.";
			executorService = Executors.newFixedThreadPool(UPDATE_THREADS);
		}

	}

	private void submitUpdateTask(final GameObject gameObject, final float t, final float dt) {
		executorService.submit(() -> {
			gameObject.preUpdate();
			gameObject.update(dt, objects);
		});
	}

	public void tick(float t, float dt) {
		if (!game.getLevelHandler().isActive() || game.getLevelHandler().isLoading()) {
			return;
		}

		if (USE_THREAD_POOL) {
			objects.forEach((object) -> submitUpdateTask(object, t, dt));
		} else {
			for (GameObject tempObject : objects) {
				tempObject.preUpdate();
				tempObject.update(dt, objects);
			}

		}

		if (shouldLoadNextLevel) {
			shouldLoadNextLevel = false;
			getLevelHandler().nextLevel();
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
			playerReference.onRender(renderer, lerp);
			camera.tick(playerReference);
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

	public void nextLevel() {
		shouldLoadNextLevel = true;
	}

	public void clearObjects() {
		playerReference = null;
		objects.clear();
	}

	public void centerOnPlayer() {
		// for (GameObject gameObject : objects) {
		// 	if (gameObject.getId() == ObjectId.Player) {
		// 		playerReference = (Player) gameObject;
		// 		break;
		// 	}
		// }
		// Vector3f position = new Vector3f().set(playerReference.getPosition());
		// System.out.println(position.toString(new DecimalFormat("0.###")));
		// objects.forEach((object) -> {
		// 	object.getPosition().sub(position);
		// });
		// System.out.println(objects.get(objects.size() - 1).getPosition().toString(new DecimalFormat("0.###")));
	}
}
