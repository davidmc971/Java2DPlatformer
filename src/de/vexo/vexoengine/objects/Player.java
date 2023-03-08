package de.vexo.vexoengine.objects;

import java.awt.Rectangle;

import static org.lwjgl.opengl.GL11.*;
import static de.vexo.vexoengine.graphics.RenderUtil.*;

import java.util.LinkedList;

import de.vexo.vexoengine.framework.GameObject;
import de.vexo.vexoengine.framework.ObjectId;
import de.vexo.vexoengine.main.Handler;

public class Player extends GameObject {

	private float width = 32, height = 64;
	private float gravity = 0.5f;
	private final float MAX_SPEED = 10;
	private Handler handler;
	private float checkX, checkY;
	private boolean cameraFocus = true;
	
	public Player(float x, float y, Handler handler, ObjectId id) {
		super(x, y, id);
		this.handler = handler;
		this.checkX = getX();
		this.checkY = getY();
		if(handler.getLevelHandler().getLevel() == 2){
			this.gravity = 0.3f;
		} else {
			this.gravity = 0.5f;
		}
	}

	public void tick(LinkedList<GameObject> object) {
		x += velX;
		y += velY;
		if(falling || jumping){
			velY += gravity;
			if(velY > MAX_SPEED){
				velY = MAX_SPEED;
			}
		}
		collision(object);
	}
	
	private void collision(LinkedList<GameObject> object){
		for(int i = 0; i < handler.object.size(); i++){
			GameObject tempObject = handler.object.get(i);
			if(tempObject.getId() == ObjectId.Block){
				if(getBoundsAll()[0].intersects(tempObject.getBounds())){
					y = tempObject.getY() - height;
					velY = 0;
					falling = false;
					jumping = false;
				} else {
					falling = true;
				}
				if(getBoundsAll()[1].intersects(tempObject.getBounds())){
					y = tempObject.getY() + tempObject.getBounds().height;
					velY = 0;
				}
				if(getBoundsAll()[2].intersects(tempObject.getBounds())){
					x = tempObject.getX() - tempObject.getBounds().width;
				}
				if(getBoundsAll()[3].intersects(tempObject.getBounds())){
					x = tempObject.getX() + tempObject.getBounds().width;
				}
			}
			if(tempObject.getId() == ObjectId.Death){
				if(		getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds()) )
				{
					this.setX(this.getCheckX());
					this.setY(this.getCheckY());
				}
				
			}
			if(tempObject.getId() == ObjectId.Check){
				if(		getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds()) )
				{
					this.setCheckX(tempObject.getX()+1);
					this.setCheckY(tempObject.getY()+1);
				}
			}
			if(tempObject.getId() == ObjectId.Finish){
				if(		getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds()) )
				{
					if(handler.getLevelHandler().isActive()){
						handler.getLevelHandler().setActive(false);
						handler.getLevelHandler().nextLevel();
					}
				}
				
			}
			if(tempObject.getId() == ObjectId.Elevator){
				if(		getBoundsAll()[0].intersects(tempObject.getBounds()) ||
						getBoundsAll()[1].intersects(tempObject.getBounds()) ||
						getBoundsAll()[2].intersects(tempObject.getBounds()) ||
						getBoundsAll()[3].intersects(tempObject.getBounds()) )
				{
					this.setVelY(this.getVelY()-0.8f);
				}
				
			}
		}
	}

	public void render() {
		color4_255(200, 100, 0, 127);
		x = (int)x;
		y = (int)y;
		width = (int)width;
		height = (int)height;
		
		glRectf(x, y, x+width, y+height);
		if(true)
		for(int i = 0; i < getBoundsAll().length; i++){
			Rectangle r = getBoundsAll()[i];
			color3_255(255/4*(i+1), 255, 255/(i+1));
			glRectf(r.x, r.y, r.x+r.width, r.y+r.height);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x, (int)y, (int)width, (int)height);
	}
	
	public Rectangle[] getBoundsAll() {
		return new Rectangle[]{ 
				new Rectangle((int)(x+width/4), (int)(y+height/2), (int)width/2, (int)height/2),//Bottom
				new Rectangle((int)(x+width/4), (int)y, (int)width/2, (int)height/2),//Top
				new Rectangle((int)(x+width-width/4), (int)(y+4), (int)width/4, (int)(height-8)),//Right
				new Rectangle((int)x, (int)(y+4), (int)width/4, (int)(height-8))};//Left
	}

	public float getCheckX() {
		return checkX;
	}

	public void setCheckX(float checkX) {
		this.checkX = checkX;
	}

	public float getCheckY() {
		return checkY;
	}

	public void setCheckY(float checkY) {
		this.checkY = checkY;
	}
	
	public boolean getFocusCamera(){
		return cameraFocus;
	}
	
	public void setCameraFocus(boolean focus){
		this.cameraFocus = focus;
	}
}
