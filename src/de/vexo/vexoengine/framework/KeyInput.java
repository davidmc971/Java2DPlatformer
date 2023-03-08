package de.vexo.vexoengine.framework;

import java.awt.event.KeyAdapter;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.vexo.vexoengine.main.Game;
import de.vexo.vexoengine.main.Handler;
import de.vexo.vexoengine.objects.Player;

public class KeyInput extends Thread {
	Handler handler;
	Game game;
	
	public KeyInput(Handler handler, Game game){
		this.handler = handler;
		this.game = game;
	}
	
	public void checkKeys(){
		
		for(int i = 0; i < handler.object.size(); i++){
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getId() == ObjectId.Player){
				if(Keyboard.isKeyDown(Keyboard.KEY_A)){
					tempObject.setVelX(-3.5f);
				} else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
					tempObject.setVelX(3.5f);
				} else tempObject.setVelX(0);
				if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !tempObject.isJumping()){
					tempObject.setVelY(-9.5f);
					tempObject.setJumping(true);
				}
				if(Mouse.isButtonDown(0)){
					((Player)tempObject).setCameraFocus(false);
				}
				if(!Mouse.isButtonDown(0)){
					((Player)tempObject).setCameraFocus(true);
				}
			}
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			game.exitGame();
		}
	}
}
