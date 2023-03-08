package de.vexo.vexoengine.objects;

import static de.vexo.vexoengine.graphics.RenderUtil.color3_255;
import static org.lwjgl.opengl.GL11.glRectf;

import java.awt.Rectangle;
import java.util.LinkedList;

import de.vexo.vexoengine.framework.GameObject;
import de.vexo.vexoengine.framework.ObjectId;

public class Enemy extends GameObject {

	public Enemy(float x, float y, ObjectId id) {
		super(x, y, id);
		
	}

	public void tick(LinkedList<GameObject> object) {
		
	}
	
	public void render(){
		if(this.getId() == ObjectId.Finish){
			color3_255(255, 255, 255);
			glRectf(x, y, x+16, y+16);
			glRectf(x+16, y+16, x+32, y+32);
			color3_255(20, 20, 20);
			glRectf(x+16, y, x+32, y+16);
			glRectf(x, y+16, x+16, y+32);
		} else if(this.getId() == ObjectId.Death) {
			color3_255(56,0,0);
			glRectf(x, y, x+32, y+32);
			color3_255(200,0,0);
			glRectf(x+2, y+2, x+30, y+30);
		} else if(this.getId() == ObjectId.Check){
			color3_255(0,10,10);
			glRectf(x, y, x+32, y+32);
			color3_255(0,170,170);
			glRectf(x+2, y+2, x+30, y+30);
		} else {
			color3_255(0,40,0);
			glRectf(x, y, x+32, y+32);
			color3_255(0,255,0);
			glRectf(x+2, y+2, x+30, y+30);
		}
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x, (int)y, 32, 32);
	}
}
