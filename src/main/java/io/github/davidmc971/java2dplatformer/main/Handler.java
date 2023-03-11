package io.github.davidmc971.java2dplatformer.main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import org.joml.Matrix4f;

import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;
import io.github.davidmc971.java2dplatformer.objects.BGBlock;
import io.github.davidmc971.java2dplatformer.objects.Block;

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

	public void render() {

		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() != ObjectId.Background) {
				if (tempObject.getId() != ObjectId.Player) {
					tempObject.render();
				}
			}
		}
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Player) {
				tempObject.render();
			}
		}
	}

	public void addObject(GameObject object) {
		this.object.add(object);
	}

	public void removeObject(GameObject object) {
		this.object.remove(object);
	}

	public void renderBG(int locModel, Matrix4f m4fModel, FloatBuffer fbModelMatrix, int positionAttrib,
			int colorAttrib) {
		for (int i = 0; i < object.size(); i++) {
			tempObject = object.get(i);

			if (tempObject.getId() == ObjectId.Background) {
				((BGBlock) tempObject).render(locModel, m4fModel, fbModelMatrix, positionAttrib, colorAttrib);
			}
		}
	}

	public LevelHandler getLevelHandler() {
		return game.getLevelHandler();
	}
}
