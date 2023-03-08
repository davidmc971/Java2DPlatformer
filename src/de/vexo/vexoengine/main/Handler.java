package de.vexo.vexoengine.main;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

import de.vexo.vexoengine.framework.GameObject;
import de.vexo.vexoengine.framework.ObjectId;
import de.vexo.vexoengine.objects.Block;
import de.vexo.vexoengine.framework.LevelHandler;

public class Handler {
	
	public LinkedList<GameObject> object = new LinkedList<GameObject>();
	
	private GameObject tempObject;
	private Game game;
	
	public Handler(Game game) {
		this.game = game;
	}

	public void tick(){
		for(int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			
			tempObject.tick(object);
		}
		
		
	}

	public void render(){
		
		for(int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			
			if(tempObject.getId() != ObjectId.Background){
				if(tempObject.getId() != ObjectId.Player){
					tempObject.render();
				}
			}
		}
		for(int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			
			if(tempObject.getId() == ObjectId.Player){
				tempObject.render();
			}
		}
	}
	
	public void addObject(GameObject object){
		this.object.add(object);
	}
	
	public void removeObject(GameObject object){
		this.object.remove(object);
	}

	public void renderBG() {
		for(int i = 0; i < object.size(); i++){
			tempObject = object.get(i);
			
			if(tempObject.getId() == ObjectId.Background){
				tempObject.render();
			}
		}
	}
	
	public LevelHandler getLevelHandler(){
		return game.getLevelHandler();
	}
}
