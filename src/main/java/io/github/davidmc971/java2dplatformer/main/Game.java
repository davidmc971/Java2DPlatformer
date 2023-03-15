package io.github.davidmc971.java2dplatformer.main;

import static io.github.davidmc971.java2dplatformer.graphics.RenderUtil.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

import io.github.davidmc971.java2dplatformer.ecs.ECSEngine;
import io.github.davidmc971.java2dplatformer.ecs.EntitySystem;
import io.github.davidmc971.java2dplatformer.ecs.components.TestComponent;
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

	public static final boolean VSYNC = false;
	public static final boolean DEBUG = true;

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

		initScene();
		// legacyLoop();
		interpolationGameLoop();
	}

	protected void legacyLoop() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double totalUpdateTime = 0;
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
				totalUpdateTime += 1f / amountOfTicks;
				update((float) totalUpdateTime, 1f / (float) amountOfTicks);
				if (glfwWindowShouldClose(window))
					running = false;
				updates++;
				delta--;
			}
			render(1f);
			frames++;

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}

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
				update((float) updateTimeTotal, (float) updateTimeStep);
				if (glfwWindowShouldClose(window))
					running = false;
			}

			renderLerp = accumulator / updateTimeStep;
			renderLerp = Math.max(Math.min(renderLerp, 1.0d), 0.0d);

			if (frameTimeAccumulator >= minFrameTime) {
				render((float) renderLerp);
				frameTimeAccumulator -= minFrameTime;
				lastFramesPerSecond++;
			}

			if (System.currentTimeMillis() - updateDisplayTimer > 1000) {
				updateDisplayTimer += 1000;
				System.out.println("FPS: " + lastFramesPerSecond + " TICKS: " + lastUpdatesPerSecond);
				lastFramesPerSecond = 0;
				lastUpdatesPerSecond = 0;
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

	private ECSEngine ecsEngine;
	private PooledEngine ashleyECSPooledEngine;

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

		ecsEngine = new ECSEngine();

		ashleyECSPooledEngine = new PooledEngine(2048, Integer.MAX_VALUE, 65536, Integer.MAX_VALUE);

		Entity entity = ashleyECSPooledEngine.createEntity();
		entity.add(ashleyECSPooledEngine.createComponent(io.github.davidmc971.java2dplatformer.ashley.components.TestComponent.class));
		ashleyECSPooledEngine.addEntity(entity);

		ecsEngine.addEntitySystem(new EntitySystem() {

			@Override
			public void initialize() {
				
			}

			@Override
			public void update(float t, float dt) {
				
				if (Math.floor(t / dt) % 50 == 0)
					System.out.println("ECS contains " + ecsEngine.getComponents(TestComponent.class).size() + " TestComponent instances.");
			}

		});

		handler = new Handler(this, ecsEngine, ashleyECSPooledEngine);
		levelh = new LevelHandler(handler, ecsEngine, ashleyECSPooledEngine);

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

	private Scene mainScene = new Scene() {

		@Override
		public void update(float t, float dt) {
			gameObjects.forEach((go) -> go.update(t, dt));
		}

	};

	private void initScene() {
		io.github.davidmc971.java2dplatformer.ecs.GameObject testObject = new io.github.davidmc971.java2dplatformer.ecs.GameObject();
		mainScene.addGameObject(testObject);
		mainScene.start();
	}

	private void update(float t, float dt) {
		ecsEngine.update(t, dt);
		mainScene.update(t, dt);
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
