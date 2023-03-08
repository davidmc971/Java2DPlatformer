package de.vexo.vexoengine.objects;

import java.awt.Rectangle;
import java.util.LinkedList;

import de.vexo.vexoengine.framework.BGType;
import de.vexo.vexoengine.framework.GameObject;
import de.vexo.vexoengine.framework.ObjectId;
import static de.vexo.vexoengine.graphics.RenderUtil.*;
import static org.lwjgl.opengl.GL11.*;

public class BGBlock extends GameObject {
	private int r, g, b;
	private float width = 32, height = 32;
	private float counter = 0;

	public BGBlock(float x, float y, int r, int g, int b) {
		super(x, y, ObjectId.Background);
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void tick(LinkedList<GameObject> object) {
		counter += 0.1;
		if(counter == 360){
			counter = 0;
		}
	}

	public void render() {
		int a = (int)Math.abs(Math.sin(counter)*255);
		a *= 0.05;
		int z = 8;
		color3_255((r+a)/z, (g+a)/z, (b+a)/z);
		glRectf(x, y, x+width, y+height);
	}

	public Rectangle getBounds() {
		return new Rectangle((int)x, (int)y, (int)width , (int)height);
	}

}
