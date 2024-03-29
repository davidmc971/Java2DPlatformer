package io.github.davidmc971.java2dplatformer.framework;

import static org.lwjgl.glfw.GLFW.*;

import io.github.davidmc971.java2dplatformer.main.Game;
import io.github.davidmc971.java2dplatformer.main.Handler;
import io.github.davidmc971.java2dplatformer.objects.Player;

public class KeyInput extends Thread {
	Handler handler;
	Game game;
	private long window;

	public KeyInput(Handler handler, Game game) {
		this.handler = handler;
		this.game = game;
	}

	public void checkKeys() {
		this.window = game.getWindow();

		for (int i = 0; i < handler.objects.size(); i++) {
			GameObject tempObject = handler.objects.get(i);

			if (tempObject.getId() == ObjectId.Player) {
				if (glfwGetKey(this.window, GLFW_KEY_A) == GLFW_PRESS) {
					tempObject.setVelX(-180f);
				} else if (glfwGetKey(this.window, GLFW_KEY_D) == GLFW_PRESS) {
					tempObject.setVelX(180f);
				} else
					tempObject.setVelX(0);
				if (glfwGetKey(this.window, GLFW_KEY_SPACE) == GLFW_PRESS && !tempObject.isJumping()) {
					tempObject.setVelY(-400f);
					tempObject.setJumping(true);
				}
				if (glfwGetKey(this.window, GLFW_KEY_L) == GLFW_PRESS) {
					while (glfwGetKey(this.window, GLFW_KEY_L) == GLFW_PRESS) {
						glfwPollEvents();
					}
					game.getLevelHandler().nextLevel();
				}
				if (glfwGetMouseButton(this.window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
					((Player) tempObject).setCameraFocus(false);
				} else {
					((Player) tempObject).setCameraFocus(true);
				}
			}
		}

		if (glfwGetKey(this.window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			game.exitGame();
		}
	}
}
