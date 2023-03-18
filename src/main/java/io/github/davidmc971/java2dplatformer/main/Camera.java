package io.github.davidmc971.java2dplatformer.main;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import io.github.davidmc971.java2dplatformer.objects.Player;

public class Camera {
	private Matrix4f m4fProjection, m4fView;
	private Vector3f v3fPosition;
	private float w = 0, h = 0;

	public Camera(Vector3f position) {
		this.v3fPosition = position;
		this.m4fProjection = new Matrix4f();
		this.m4fView = new Matrix4f();
	}

	private float l, r, b, t, zoom = 1.5f, zfW, zfH;

	public void setupOrtho(float width, float height) {
		w = width;
		h = height;
		m4fProjection.identity();
		zfW = width - width / zoom;
		zfH = height - height / zoom;
		// Zoom 1 | Zoom 1.5
		// 1280 -> 0 | 1280 -> 320
		l = 0 + zfW / 2f;
		// 1280 -> 1280 | 1280 -> 960
		r = width - zfW / 2f;
		// 1280 -> 0 | 1280 -> 320
		b = height - zfH / 2f;
		// 1280 -> 0 | 1280 -> 320
		t = 0 + zfH / 2f;
		m4fProjection.ortho(l, r, b, t, -Short.MAX_VALUE, Short.MAX_VALUE);
	}

	public boolean coordsVisible2D(float x, float y, float qw, float qh) {
		// if (Game.drawCalls % 20 < 2) {
		// System.out.println("Camera: " + -v3fPosition.x + ", " + -v3fPosition.y);
		// System.out.println("Check: " + x + ", " + y);
		// }
		return Math.abs((-v3fPosition.x + (w / 2)) - (x + qw / 2)) < w / 2 / zoom + qw / 2
				&& Math.abs((-v3fPosition.y + (h / 2)) - (y + qh / 2)) < h / 2 / zoom + qh / 2;
	}

	public Matrix4f getViewMatrix() {
		// Vector3f cameraFront = new Vector3f(0, 0, -1);
		// Vector3f cameraUp = new Vector3f(0, 1, 0);
		// m4fView.identity();
		// m4fView = m4fView.lookAt(new Vector3f(v3fPosition.x, v3fPosition.y, 20f),
		// cameraFront.add(v3fPosition.x, v3fPosition.y, 0), cameraUp);

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

	public float getX() {
		return v3fPosition.x;
	}

	public float getY() {
		return v3fPosition.y;
	}

	public void tick(Player player) {
		if (player.getFocusCamera()) {
			v3fPosition.x = -(player.getInterpolatedPosition().x) + w / 2f
					- player.getDimensions().x / 2f;
			v3fPosition.y = -(player.getInterpolatedPosition().y) + h / 2f
					- player.getDimensions().y / 2f;
		} // else {
			// glfwGetCursorPos(window, mouseXBuf, mouseYBuf);
			// this.x = -player.getX() + Game.WIDTH / 2 - player.getBounds().width / 2 +
			// ((float) mouseXBuf.get()) - Game.WIDTH / 2;
			// this.y = -player.getY() + Game.HEIGHT / 2 - player.getBounds().height / 2 -
			// ((float) mouseYBuf.get()) + Game.HEIGHT / 2;
			// }
	}

	private Matrix4f conversionMatrix = new Matrix4f();

	public Vector3fc screenPositionToWorldPosition(Vector2fc screenPosition) {
		conversionMatrix.identity().mul(m4fProjection).mul(m4fView).invert();
		return conversionMatrix.transformPosition(new Vector3f(screenPosition.x(), screenPosition.y(), 0));
	}

	public Vector2fc worldPositionToScreenPosition(Vector3fc worldPosition) {
		// TODO:
		return new Vector2f();
	}
}
