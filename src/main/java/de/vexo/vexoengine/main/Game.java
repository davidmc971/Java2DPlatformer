package de.vexo.vexoengine.main;

import static de.vexo.vexoengine.graphics.RenderUtil.*;
// import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.BufferedReader;
import java.util.Random;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import de.vexo.vexoengine.framework.KeyInput;
import de.vexo.vexoengine.framework.LevelHandler;
import de.vexo.vexoengine.framework.ObjectId;
import de.vexo.vexoengine.objects.Player;

public class Game implements Runnable {
	public static int WIDTH, HEIGHT;
	// private static final long serialVersionUID = 7492659545089075909L;
	private boolean running = false;
	private Thread thread;
	private int w = 0, h = 0;
	private String title = "";

	private Handler handler;
	private LevelHandler levelh;
	private KeyInput keyInput;
	private Camera cam;
	private Random rand = new Random();

	// Some working memory for JOML and our shader
	private FloatBuffer fbProjectionMatrix;
	private FloatBuffer fbViewMatrix;
	private FloatBuffer fbModelMatrix;
	private Matrix4f m4fProjection;
	private Matrix4f m4fView;
	private Matrix4f m4fModel;

	public synchronized void start(int w, int h, String title) {
		if (running)
			return;

		this.w = w;
		this.h = h;
		this.title = title;

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		// try {
		// Display.setDisplayMode(new DisplayMode(w, h));
		// Display.create();
		// Display.setTitle(title);
		// } catch (LWJGLException e) {
		// e.printStackTrace();
		// Display.destroy();
		// System.exit(1);
		// }
		init();
		initGL();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				tick();
				if (glfwWindowShouldClose(window))
					running = false;
				updates++;
				delta--;
			}
			render();
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}

	// Keeping a strong reference to the error callback so it does not get GC'd
	private GLFWErrorCallback errorCallback;
	private long window;

	public long getWindow() {
		return window;
	}

	private void init() {
		fbProjectionMatrix = MemoryUtil.memAllocFloat(16);
		fbViewMatrix = MemoryUtil.memAllocFloat(16);
		fbModelMatrix = MemoryUtil.memAllocFloat(16);
		m4fProjection = new Matrix4f();
		m4fView = new Matrix4f();
		m4fModel = new Matrix4f();

		glfwInit();
		// Setup an error callback to print GLFW errors to the console.
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		// this.WIDTH = Display.getWidth();
		// this.HEIGHT = Display.getHeight();
		long monitor = 0;

		window = glfwCreateWindow(this.w, this.h, this.title, monitor, 0);

		if (window == 0) {
			throw new RuntimeException("Failed to create window");
		}

		// Make this window's context the current on this thread.
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);

		glfwShowWindow(window);

		handler = new Handler(this);
		levelh = new LevelHandler(this, handler);

		levelh.loadLevel(1);

		cam = new Camera(0, 0, 0, w, h, window);

		// handler.addObject(new Player(100, 100, handler, ObjectId.Player));

		// handler.createLevel();

		keyInput = new KeyInput(handler, this);
	}

	private int program, vao, vbo, ebo;
	private int locProjection;
	private int locView;
	private int locModel;

	private float[] vertexArray = {
			// position // color
			0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 0
			-0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, // 1
			0.5f, 0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, // 2
			-0.5f, -0.5f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f // 3
	};

	// counter clockwise
	private int[] elementArray = {
			2, 1, 0,
			0, 1, 3
	};

	private void initGL() {
		GL.createCapabilities();
		clearColor3_255(0, 0, 20);

		StringBuilder stringBuilder = new StringBuilder();
		String line = "";

		CharSequence vertexSource, fragmentSource;

		try {
			BufferedReader vertexReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(getClass().getResource("/shaders/main.vert").getPath())));

			BufferedReader fragmentReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(getClass().getResource("/shaders/main.frag").getPath())));

			while ((line = vertexReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			vertexSource = stringBuilder.toString();

			stringBuilder = new StringBuilder();

			while ((line = fragmentReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			fragmentSource = stringBuilder.toString();

		} catch (Exception e) {
			e.printStackTrace();
			exitGame();
			return;
		}

		// Create shader program
		program = glCreateProgram();

		// Load and compile vertex shader
		int vertexId = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexId, vertexSource);
		glCompileShader(vertexId);
		if (glGetShaderi(vertexId, GL_COMPILE_STATUS) != GL_TRUE) {
			System.out.println(glGetShaderInfoLog(vertexId, Integer.MAX_VALUE));
			throw new RuntimeException();
		}

		// Load and compile fragment shader
		int fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentId, fragmentSource);
		glCompileShader(fragmentId);
		if (glGetShaderi(fragmentId, GL_COMPILE_STATUS) != GL_TRUE) {
			System.out.println(glGetShaderInfoLog(fragmentId, Integer.MAX_VALUE));
			throw new RuntimeException();
		}

		// Attach vertex and fragment shader to program
		glAttachShader(program, vertexId);
		glAttachShader(program, fragmentId);

		// Link program and verify its status
		glLinkProgram(program);
		if (glGetProgrami(program, GL_LINK_STATUS) != GL_TRUE) {
			System.out.println(glGetProgramInfoLog(program, Integer.MAX_VALUE));
			throw new RuntimeException();
		}

		// Model location reference to inside shader program
		locModel = glGetUniformLocation(program, "model");
		if (locModel == -1) {
			throw new RuntimeException();
		}

		// View location reference to inside shader program
		locView = glGetUniformLocation(program, "view");
		if (locView == -1) {
			throw new RuntimeException();
		}

		// Projection location reference to inside shader program
		locProjection = glGetUniformLocation(program, "projection");
		if (locProjection == -1) {
			throw new RuntimeException();
		}

		// Generate VAO, VBO, EBO
		vao = glGenVertexArrays();
		glBindVertexArray(vao);

		// FB of vertices
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip();

		// VBO
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Indices
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		ebo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

		// vertex arttrib pointer
		int positionsSize = 3;
		int colorSize = 4;
		int floatSizeBytes = 4;
		int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
		glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
		glEnableVertexAttribArray(1);
	}

	private void tick() {
		handler.tick();
		keyInput.checkKeys();
		for (int i = 0; i < handler.object.size(); i++) {
			if (handler.object.get(i).getId() == ObjectId.Player)
				cam.tick((Player) handler.object.get(i));
		}
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(program);
		glBindVertexArray(vao);

		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		m4fProjection.identity();
				//.ortho2D(0, w, h, 0).translate(cam.getX(), cam.getY(), 0);
		m4fView.identity();
		m4fModel.identity();
		glUniformMatrix4fv(locProjection, false, m4fProjection.get(fbProjectionMatrix));
		glUniformMatrix4fv(locView, false, m4fView.get(fbViewMatrix));
		glUniformMatrix4fv(locModel, false, m4fModel.get(fbModelMatrix));

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);

		// //! Render 2D Code
		// glTranslatef(cx, cy, 0);
		// glEnable(GL_BLEND);
		// glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		// handler.renderBG();
		// handler.render();
		// glDisable(GL_BLEND);
		// glTranslatef(-cx, -cy, 0);

		glBindVertexArray(0);
		glUseProgram(0);

		// Swaps framebuffers.
		glfwSwapBuffers(window);
		// Polls input.
		glfwPollEvents();
	}

	public void exitGame() {
		running = false;
		glfwDestroyWindow(window);
		glfwTerminate();
		MemoryUtil.memFree(fbProjectionMatrix);
		MemoryUtil.memFree(fbViewMatrix);
		MemoryUtil.memFree(fbModelMatrix);
		// Display.destroy();
		System.exit(0);
	}

	public LevelHandler getLevelHandler() {
		return levelh;
	}
}
