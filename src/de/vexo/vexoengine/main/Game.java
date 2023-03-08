package de.vexo.vexoengine.main;

import static de.vexo.vexoengine.graphics.RenderUtil.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLUConstants;

import de.vexo.vexoengine.framework.KeyInput;
import de.vexo.vexoengine.framework.LevelHandler;
import de.vexo.vexoengine.framework.ObjectId;
import de.vexo.vexoengine.graphics.BufferedImageLoader;
import de.vexo.vexoengine.objects.BGBlock;
import de.vexo.vexoengine.objects.Block;
import de.vexo.vexoengine.objects.Player;

public class Game implements Runnable {
	public static int WIDTH, HEIGHT;
	//private static final long serialVersionUID = 7492659545089075909L;
	private boolean running = false;
	private Thread thread;
	private int w = 0, h= 0;
	private String title = "";
	
	private Handler handler;
	private LevelHandler levelh;
	private KeyInput keyInput;
	private Camera cam;
	private Random rand = new Random();
	
	public synchronized void start(int w, int h, String title){
		if(running)
			return;
		
		this.w = w;
		this.h = h;
		this.title = title;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		try {
			Display.setDisplayMode(new DisplayMode(w, h));
			Display.create();
			Display.setTitle(title);
		} catch (LWJGLException e) {
			e.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		init();
		initGL();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int updates = 0;
		int frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				tick();
				if(Display.isCloseRequested())
					running = false;
				updates++;
				delta--;
			}
			render();
			frames++;
					
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println("FPS: " + frames + " TICKS: " + updates);
				frames = 0;
				updates = 0;
			}
		}
	}
	
	private void init() {
		this.WIDTH = Display.getWidth();
		this.HEIGHT = Display.getHeight();
		
		handler = new Handler(this);
		levelh = new LevelHandler(this, handler);
		
		levelh.loadLevel(1);
		
		cam = new Camera(0, 0, 0);
		
		//handler.addObject(new Player(100, 100, handler, ObjectId.Player));
		
		//handler.createLevel();
		
		keyInput = new KeyInput(handler, this);
	}
	
	private void initGL() {
		clearColor3_255(0, 0, 20);
		
		
		
		
		
	}

	private void tick() {
		handler.tick();
		keyInput.checkKeys();
		for(int i = 0; i < handler.object.size(); i++){
			if(handler.object.get(i).getId() == ObjectId.Player)
				cam.tick((Player)handler.object.get(i));
		}
		
	}
	
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		cam.initCam2D();
		glLoadIdentity();

		float cx = (float)((int)cam.getX());
		float cy = (float)((int)cam.getY());
		
		//Render 2D Code
		glTranslatef(cx, cy, 0);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		handler.renderBG();
		handler.render();	
		glDisable(GL_BLEND);
		glTranslatef(-cx, -cy, 0);
		/*
		cam.initCam3D();
		glLoadIdentity();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		
		glBegin(GL_QUAD_STRIP);
		color3_255(255, 255, 255);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 1);
		glVertex3f(0, 1, 0);
		glVertex3f(0, 1, 1);
		glVertex3f(1, 0, 0);
		glVertex3f(1, 0, 1);
		glVertex3f(1, 1, 0);
		glVertex3f(1, 0, 1);
		glEnd();
		
		glDisable(GL_DEPTH_TEST);
		*/
		Display.update();
		Display.sync(60);
	}

	public void exitGame() {
		running = false;
		Display.destroy();
		System.exit(0);
	}
	
	public LevelHandler getLevelHandler(){
		return levelh;
	}
}
