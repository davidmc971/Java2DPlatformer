package io.github.davidmc971.java2dplatformer.objects;

import java.awt.Rectangle;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import io.github.davidmc971.java2dplatformer.framework.BGType;
import io.github.davidmc971.java2dplatformer.framework.GameObject;
import io.github.davidmc971.java2dplatformer.framework.ObjectId;

import static io.github.davidmc971.java2dplatformer.graphics.RenderUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

class BGBuffer {
	static BGBuffer instance = null;

	static BGBuffer Instance() {
		return instance != null ? instance : (instance = new BGBuffer());
	}

	public final FloatBuffer vertexBuffer;
	public final IntBuffer indexBuffer;
	public final FloatBuffer colorBuffer;
	public final int ebo;

	private BGBuffer() {
		vertexBuffer = BufferUtils.createFloatBuffer(16);
		indexBuffer = BufferUtils.createIntBuffer(16);
		colorBuffer = BufferUtils.createFloatBuffer(16);
		ebo = glGenBuffers();
	}

}

public class BGBlock extends GameObject {

	private int r, g, b;
	private float width = 32, height = 32;
	private float counter = 0;

	private BGBuffer bgBuffer = BGBuffer.Instance();

	float[] vertices = {
			0.0f, 0.0f, 0.0f,
			width, 0.0f, 0.0f,
			width, height, 0.0f,
			0.0f, height, 0.0f
	};

	int[] indices = {
			0, 1, 2,
			2, 0, 3
	};

	float[] colors;

	public BGBlock(float x, float y, int r, int g, int b) {
		super(x, y, ObjectId.Background);
		colors = new float[] {
				r, g, b, 1.0f,
				r, g, b, 1.0f,
				r, g, b, 1.0f,
				r, g, b, 1.0f
		};

	}

	public void tick(LinkedList<GameObject> object) {
		counter += 0.1;
		if (counter == 360) {
			counter = 0;
		}
	}

	public void render(int locModel, Matrix4f m4fModel,
			FloatBuffer fbModelMatrix, int positionAttrib, int colorAttrib) {
		int a = (int) Math.abs(Math.sin(counter) * 255);
		a *= 0.05;
		int z = 8;
		// color3_255((r+a)/z, (g+a)/z, (b+a)/z);
		// glRectf(x, y, x+width, y+height);

	  bgBuffer.vertexBuffer.clear();
	  bgBuffer.indexBuffer.clear();
	  bgBuffer.colorBuffer.clear();

		bgBuffer.vertexBuffer.put(vertices);
		bgBuffer.indexBuffer.put(indices);
		bgBuffer.colorBuffer.put(colors);

		glEnableVertexAttribArray(positionAttrib);
    glEnableVertexAttribArray(colorAttrib);

		glVertexAttribPointer(positionAttrib, 3, GL_FLOAT, false, 0, bgBuffer.vertexBuffer.rewind());
		glVertexAttribPointer(colorAttrib, 4, GL_FLOAT, false, 0, bgBuffer.colorBuffer.rewind());

		m4fModel.identity().translate(new Vector3f(x, y, 0));
		glUniformMatrix4fv(locModel, false, m4fModel.get(fbModelMatrix));

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bgBuffer.ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, bgBuffer.indexBuffer.rewind(), GL_DYNAMIC_DRAW);
		
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

		glDisableVertexAttribArray(positionAttrib);
    glDisableVertexAttribArray(colorAttrib);
	}

	public Rectangle getBounds() {
		return new Rectangle((int) x, (int) y, (int) width, (int) height);
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'render'");
	}

}
