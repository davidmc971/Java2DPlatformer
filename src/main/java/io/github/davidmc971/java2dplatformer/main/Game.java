package io.github.davidmc971.java2dplatformer.main;

import static io.github.davidmc971.java2dplatformer.graphics.RenderUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import io.github.davidmc971.java2dplatformer.framework.KeyInput;
import io.github.davidmc971.java2dplatformer.framework.LevelHandler;
import io.github.davidmc971.java2dplatformer.rendering.Renderer;
import io.github.davidmc971.java2dplatformer.rendering.Texture;

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

	public static final boolean DEBUG = true;
	public static final boolean VSYNC = false;
	public static final boolean ENABLE_FRAME_LIMITER = false;
	public static final int MAX_FRAMES_PER_SECOND = 300;

	public synchronized void start(int w, int h, String title) {
		if (running)
			return;

		this.w = w;
		this.h = h;
		this.title = title;

		WIDTH = w;
		HEIGHT = h;

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		init();
		initGL();

		System.out.println("Max texture slots: " + Texture.getMaxTextureSlots());
		System.out.println("Max texture size: " + Texture.getMaxTextureSize());
		System.out.println("Max texture array layers: " + Texture.getMaxTextureArrayLayers());

		interpolationGameLoop();
	}

	public static int drawCalls = 0;

	protected void interpolationGameLoop() {
		long lastTime = System.nanoTime();
		double maxFramesPerSecond = 300;
		double minFrameTime = 1 / maxFramesPerSecond;
		double frameTimeAccumulator = 0;
		double updatesPerSecond = 50;
		double updateTimeStep = 1 / updatesPerSecond;
		double frameTime = 0;
		long updateDisplayTimer = System.currentTimeMillis();
		int lastUpdatesPerSecond = 0;
		int lastFramesPerSecond = 0;
		long now = 0;
		double accumulator = 0;
		double renderLerp = 0;
		double updateTimeTotal = 0;
		float lastRenderTimeMs = 0;
		float avgRenderTimeMs = 0;
		float lastUpdateTimeMs = 0;
		float avgUpdateTimeMs = 0;
		while (running) {
			now = System.nanoTime();
			frameTime = (now - lastTime) / 1_000_000_000d;
			lastTime = now;
			accumulator += frameTime;
			frameTimeAccumulator += frameTime;

			while (accumulator >= updateTimeStep) {
				lastUpdatesPerSecond++;
				accumulator -= updateTimeStep;
				updateTimeTotal += updateTimeStep;
				now = System.nanoTime();
				update((float) updateTimeTotal, (float) updateTimeStep);
				lastUpdateTimeMs = (System.nanoTime() - now) / 1_000_000f;
				avgUpdateTimeMs += lastUpdateTimeMs;
				if (glfwWindowShouldClose(window))
					running = false;
			}

			renderLerp = accumulator / updateTimeStep;
			renderLerp = Math.max(Math.min(renderLerp, 1.0d), 0.0d);

			if (!ENABLE_FRAME_LIMITER) {
				now = System.nanoTime();
				render((float) renderLerp);
				lastRenderTimeMs = (System.nanoTime() - now) / 1_000_000f;
				avgRenderTimeMs += lastRenderTimeMs;
				lastFramesPerSecond++;
			} else if (frameTimeAccumulator >= minFrameTime) {
				now = System.nanoTime();
				render((float) renderLerp);
				lastRenderTimeMs = (System.nanoTime() - now) / 1_000_000f;
				avgRenderTimeMs += lastRenderTimeMs;
				frameTimeAccumulator -= minFrameTime;
				lastFramesPerSecond++;
			}

			if (System.currentTimeMillis() - updateDisplayTimer > 1000) {
				avgUpdateTimeMs /= (float) lastUpdatesPerSecond;
				avgRenderTimeMs /= (float) lastFramesPerSecond;
				updateDisplayTimer += 1000;
				System.out.println("> FPS: " + lastFramesPerSecond
						+ "  \tAvg Frame Time:   " + avgRenderTimeMs + "ms"
						+ "  \tDraw Calls: " + drawCalls
						+ "\n  UPS: " + lastUpdatesPerSecond
						+ "  \tAvg Update Time:  " + avgUpdateTimeMs + "ms"
						+ "  \tGameObjectCount: " + handler.objects.size());
				avgUpdateTimeMs = 0;
				avgRenderTimeMs = 0;
				lastFramesPerSecond = 0;
				lastUpdatesPerSecond = 0;
				drawCalls = 0;
			}
		}
	}

	// Keeping a strong reference to the error callback so it does not get GC'd
	// The protected modifier is to avoid warning about variable being unused
	protected GLFWErrorCallback errorCallback;
	private long window;

	public long getWindow() {
		return window;
	}

	private void init() {
		glfwInit();
		// Setup an error callback to print GLFW errors to the console.
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		long monitor = 0;

		window = glfwCreateWindow(this.w, this.h, this.title, monitor, 0);

		if (window == 0) {
			throw new RuntimeException("Failed to create window");
		}

		// Make this window's context the current on this thread.
		glfwMakeContextCurrent(window);
		glfwSwapInterval(VSYNC ? 1 : 0);

		glfwShowWindow(window);

		handler = new Handler(this);
		levelh = new LevelHandler(handler);

		levelh.loadLevel(1);

		cam = new Camera(new Vector3f(0, 0, 0));
		cam.setupOrtho(w, h);

		keyInput = new KeyInput(handler, this);
	}

	private Renderer renderer = new Renderer();

	private void initGL() {
		GL.createCapabilities();
		clearColor3_255(0, 0, 20);

		renderer.initialize(cam);
	}

	private void update(float t, float dt) {
		handler.tick(t, dt);
		keyInput.checkKeys();
	}

	private void render(float renderLerp) {
		renderer.preFrame();

		// renderer.queueTestSquare();
		// renderer.flush();

		// //! Render 2D Code
		handler.renderBG(renderer, renderLerp);
		handler.render(renderer, renderLerp, cam);

		renderer.flush();

		renderer.postFrame();

		// Swaps framebuffers.
		glfwSwapBuffers(window);
		// Polls input.
		glfwPollEvents();
	}

	public void exitGame() {
		running = false;
		glfwDestroyWindow(window);
		glfwTerminate();
		// Display.destroy();
		System.exit(0);
	}

	public LevelHandler getLevelHandler() {
		return levelh;
	}
}
