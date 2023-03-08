package de.vexo.vexoengine.graphics;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;

public class RenderUtil {
	public static void color3_255(int r, int g, int b){
		glColor3f((float)r/255, (float)g/255, (float)b/255);
	}
	public static void color4_255(int r, int g, int b, int a){
		glColor4f((float)r/255, (float)g/255, (float)b/255, (float)a/255);
	}
	public static void clearColor3_255(int r, int g, int b){
		glClearColor((float)r/255, (float)g/255, (float)b/255, 0f);
	}
}
