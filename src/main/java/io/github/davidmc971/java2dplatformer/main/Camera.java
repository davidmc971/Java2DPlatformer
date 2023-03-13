package io.github.davidmc971.java2dplatformer.main;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import io.github.davidmc971.java2dplatformer.objects.Player;


public class Camera {
	private Matrix4f m4fProjection, m4fView;
	private Vector3f v3fPosition; //, v3fRotation;

	public Camera(Vector3f position) {
		this.v3fPosition = position;
		// this.v3fRotation = new Vector3f();
		this.m4fProjection = new Matrix4f();
		this.m4fView = new Matrix4f();
	}

	public void setupOrtho(float width, float height) {
		m4fProjection.identity();
		m4fProjection.ortho(0, width, height, 0, -1, 1000);
	}

	public Matrix4f getViewMatrix() {
		// Vector3f cameraFront = new Vector3f(0, 0, -1);
		// Vector3f cameraUp = new Vector3f(0, 1, 0);
		// m4fView.identity();
		// m4fView = m4fView.lookAt(new Vector3f(v3fPosition.x, v3fPosition.y, 20f),
		// 		cameraFront.add(v3fPosition.x, v3fPosition.y, 0), cameraUp);

		m4fView.identity();
		m4fView.translate(v3fPosition);
		return m4fView;
	}

	public Matrix4f getProjectionMatrix() {
		return m4fProjection;
	}

	public void translate(Vector3f translation) {
		v3fPosition.add(translation);
	}

	// private float x, y, z;
	// private float rx, ry, rz;
	// private float fov = 90;
	// private float aspectRatio;
	// private float zNear = 0.1f;
	// private float zFar = 1000f;
	// private int displayWidth, displayHeight;
	// private long window;

	// public Camera(float x, float y, float z, int displayWidth, int displayHeight,
	// long window) {
	// this.x = x;
	// this.y = y;
	// this.z = z;
	// rx = 0;
	// ry = 0;
	// rz = 0;
	// this.displayWidth = displayWidth;
	// this.displayHeight = displayHeight;
	// this.window = window;
	// aspectRatio = (float) displayWidth / (float) displayHeight;
	// }

	// public void use() {
	// glRotatef(rx, 1, 0, 0);
	// glRotatef(ry, 0, 1, 0);
	// glRotatef(rz, 0, 0, 1);
	// glTranslatef(x, y, z);
	// }

	// public float getRx() {
	// return rx;
	// }

	// public void setRx(float rx) {
	// this.rx = rx;
	// }

	// public float getRy() {
	// return ry;
	// }

	// public void setRy(float ry) {
	// this.ry = ry;
	// }

	// public float getRz() {
	// return rz;
	// }

	// public void setRz(float rz) {
	// this.rz = rz;
	// }

	// public float getFov() {
	// return fov;
	// }

	// public void setFov(float fov) {
	// this.fov = fov;
	// }

	// public float getzNear() {
	// return zNear;
	// }

	// public void setzNear(float zNear) {
	// this.zNear = zNear;
	// }

	// public float getzFar() {
	// return zFar;
	// }

	// public void setzFar(float zFar) {
	// this.zFar = zFar;
	// }

	// // public void initCam2D() {
	// // glMatrixMode(GL_PROJECTION);
	// // glLoadIdentity();
	// // glOrtho(0, displayWidth, displayHeight, 0, 1, -1);
	// // // gluPerspective(90f, aspectRatio, 0.1f, 1000f);
	// // glMatrixMode(GL_MODELVIEW);
	// // }

	// // public void initCam3D(){
	// // glMatrixMode(GL_PROJECTION);
	// // glLoadIdentity();
	// // //glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
	// // gluPerspective(90f, aspectRatio, 0.1f, 1000f);
	// // glMatrixMode(GL_MODELVIEW);
	// // }

	// public float getX() {
	// return x;
	// }

	// public void setX(float x) {
	// this.x = x;
	// }

	// public float getY() {
	// return y;
	// }

	// public void setY(float y) {
	// this.y = y;
	// }

	// public float getZ() {
	// return z;
	// }

	// public void setZ(float z) {
	// this.z = z;
	// }

	// private DoubleBuffer mouseXBuf = DoubleBuffer.allocate(2);
	// private DoubleBuffer mouseYBuf = DoubleBuffer.allocate(2);

	public void tick(Player player) {
		if (player.getFocusCamera()) {
			v3fPosition.x = -player.getX() + Game.WIDTH / 2 - player.getBounds().width / 2;
			v3fPosition.y = -player.getY() + Game.HEIGHT / 2 - player.getBounds().height / 2;
		} // else {
		// 	glfwGetCursorPos(window, mouseXBuf, mouseYBuf);
		// 	this.x = -player.getX() + Game.WIDTH / 2 - player.getBounds().width / 2 +
		// 			((float) mouseXBuf.get()) - Game.WIDTH / 2;
		// 	this.y = -player.getY() + Game.HEIGHT / 2 - player.getBounds().height / 2 -
		// 			((float) mouseYBuf.get()) + Game.HEIGHT / 2;
		// }
	}

}
