package de.vexo.vexoengine.framework;

import java.awt.Rectangle;
import java.util.LinkedList;

public abstract class GameObject {
	
	protected float x, y, velX = 0, velY = 0;
	protected boolean falling = true, jumping = false;
	
	protected ObjectId id;
	
	public GameObject(float x, float y, ObjectId id){
		this.x = x;
		this.y = y;
		this.id = id;
	}
	
	public abstract void tick(LinkedList<GameObject> object);
	public abstract void render();
	public abstract Rectangle getBounds();

	public float getX(){
		return x;
	}
	public void setX(float x){
		this.x = x;
	}
	public float getY(){
		return y;
	}
	public void setY(float y){
		this.y = y;
	}
	public float getVelX(){
		return velX;
	}
	public void setVelX(float velX){
		this.velX = velX;
	}
	public float getVelY(){
		return velY;
	}
	public void setVelY(float velY){
		this.velY = velY;
	}
	public ObjectId getId(){
		return id;
	}
	public boolean isFalling() {
		return falling;
	}
	public void setFalling(boolean falling) {
		this.falling = falling;
	}
	public boolean isJumping() {
		return jumping;
	}
	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}
}
