package de.vexo.vexoengine.main;

import static org.lwjgl.opengl.GL11.*;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

import de.vexo.vexoengine.framework.GameObject;
import de.vexo.vexoengine.objects.Player;

public class Camera {
	private float x, y, z;
	private float rx, ry, rz;
	private float fov = 90;
	private float aspectRatio;
	private float zNear = 0.1f;
	private float zFar = 1000f;
	private int displayWidth, displayHeight;
	private long window;

	public Camera(float x, float y, float z, int displayWidth, int displayHeight, long window) {
		this.x = x;
		this.y = y;
		this.z = z;
		rx = 0;
		ry = 0;
		rz = 0;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.window = window;
		aspectRatio = (float) displayWidth / (float) displayHeight;
	}

	public void use() {
		glRotatef(rx, 1, 0, 0);
		glRotatef(ry, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
		glTranslatef(x, y, z);
	}

	public float getRx() {
		return rx;
	}

	public void setRx(float rx) {
		this.rx = rx;
	}

	public float getRy() {
		return ry;
	}

	public void setRy(float ry) {
		this.ry = ry;
	}

	public float getRz() {
		return rz;
	}

	public void setRz(float rz) {
		this.rz = rz;
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public float getzNear() {
		return zNear;
	}

	public void setzNear(float zNear) {
		this.zNear = zNear;
	}

	public float getzFar() {
		return zFar;
	}

	public void setzFar(float zFar) {
		this.zFar = zFar;
	}

	// public void initCam2D() {
	// 	glMatrixMode(GL_PROJECTION);
	// 	glLoadIdentity();
	// 	glOrtho(0, displayWidth, displayHeight, 0, 1, -1);
	// 	// gluPerspective(90f, aspectRatio, 0.1f, 1000f);
	// 	glMatrixMode(GL_MODELVIEW);
	// }

	// public void initCam3D(){
	// glMatrixMode(GL_PROJECTION);
	// glLoadIdentity();
	// //glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
	// gluPerspective(90f, aspectRatio, 0.1f, 1000f);
	// glMatrixMode(GL_MODELVIEW);
	// }

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	private DoubleBuffer mouseXBuf = DoubleBuffer.allocate(2);
	private DoubleBuffer mouseYBuf = DoubleBuffer.allocate(2);

	public void tick(Player player) {
		if (player.getFocusCamera()) {
			this.x = -player.getX() + Game.WIDTH / 2 - player.getBounds().width / 2;
			this.y = -player.getY() + Game.HEIGHT / 2 - player.getBounds().height / 2;
		} else {
			glfwGetCursorPos(window, mouseXBuf, mouseYBuf);
			this.x = -player.getX() + Game.WIDTH / 2 - player.getBounds().width / 2 + ((float) mouseXBuf.get()) - Game.WIDTH / 2;
			this.y = -player.getY() + Game.HEIGHT / 2 - player.getBounds().height / 2 - ((float) mouseYBuf.get()) + Game.HEIGHT / 2;
		}
	}

}
