package de.vexo.vexoengine.framework;

import java.awt.image.BufferedImage;

import de.vexo.vexoengine.graphics.BufferedImageLoader;
import de.vexo.vexoengine.main.Game;
import de.vexo.vexoengine.main.Handler;
import de.vexo.vexoengine.objects.Block;
import de.vexo.vexoengine.objects.Player;

public class LevelHandler {
	private BufferedImage level = null;
	private Game game;
	private Handler handler;
	private BufferedImageLoader loader;
	private int numLevel = 0;
	private boolean active;
	private boolean loading;
	
	public LevelHandler(Game game, Handler handler){
		this.game = game;
		this.handler = handler;
		active = true;
		loader = new BufferedImageLoader();
	}
	
	public boolean loadLevel(int numLevel){
		this.numLevel = numLevel;
		System.out.println("Level "+numLevel);
		try{
			level = loader.loadImage("/levels/level"+numLevel+".png");
			loadImageLevel(level);
		} catch(Exception e){
			System.out.println("Level existiert nicht.");
			return false;
		}
		return true;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private void loadImageLevel(BufferedImage image){
		int w = image.getWidth();
		int h = image.getHeight();
		
		for(int i = 0; i < h; i++){
			for(int j = 0; j < w; j++){
				int pixel = image.getRGB(i, j);
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				
				/*	Normale Blöcke sind schwarz (0,0,0);
				 * 	Der Spielerspawn ist rot (255,0,0);
				 * 	Der Hintergrund ist weiß (255,255,255);
				 * 	Das Ziel ist grün (0,255,0);
				 * 	Blöcke, auf denen man stirbt sind gelb (255,255,0);
				 * 	Checkpoints sind cyan (0,255,255);
				 * 	Elevator blau 0 0 255
				 */
				
				if(red == 0 && green == 0 && blue == 0){
					handler.addObject(new Block(i*32, j*32, ObjectId.Block));
				} else if(red == 150 && green == 150 && blue == 150){
					//handler.addObject(new Player(i*32, j*32, handler, ObjectId.Player));
				} else if(red == 0 && green == 255 && blue == 0){
					handler.addObject(new Block(i*32, j*32, ObjectId.Finish));
				}/* else if(red == 0 && green == 255 && blue == 255){
					handler.addObject(new BGBlock(i*32, j*32, red, green, blue));
				} else if(red == 0 && green == 0 && blue == 255){
					handler.addObject(new BGBlock(i*32, j*32, red, green, blue));
				} else if(red == 255 && green == 255 && blue == 0){
					handler.addObject(new BGBlock(i*32, j*32, red, green, blue));
				}*/ else if(red == 255 && green == 0 && blue == 0){
					handler.addObject(new Player(i*32, j*32, handler, ObjectId.Player));
				} else if(red == 255 && green == 255 && blue == 0){
					handler.addObject(new Block(i*32, j*32, ObjectId.Death));
				} else if(red == 0 && green == 255 && blue == 255){
					handler.addObject(new Block(i*32, j*32, ObjectId.Check));
				} else if(red == 0 && green == 0 && blue == 255){
					handler.addObject(new Block(i*32, j*32, ObjectId.Elevator));
				} else if(red == 255 && green == 0 && blue == 255){
					handler.addObject(new Block(i*32, j*32, ObjectId.Enemy));
				}
				
			}
		}
	}

	public boolean nextLevel() {
		if(!loading){
			loading = true;
			disposeLevel();
			if(loadLevel(this.numLevel+1)){
				this.setActive(true);
				loading = false;
				return true;
			}
			finish();
			loading = false;
			return false;
		} else {
			loading = false;
			return false;
		}
	}

	public void finish() {
		disposeLevel();
		System.out.println("Ziel erreicht!");
	}
	
	public void disposeLevel(){
		handler.object.clear();
	}

	public int getLevel(){
		return numLevel;
	}
}
