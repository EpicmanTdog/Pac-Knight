package com.edisco;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class Sprite { //Inherited Sprite class for the characters
	
	SpriteSheet chars; //Spritesheet
	
	enum Direction{UP, DOWN, LEFT, RIGHT}; //Direction for sprites to be looking
	Direction direction = Direction.RIGHT;
	
	float x, y;
	float centerX, centerY;
	float x2, y2;
	float speed;
	
	public void init(){ 			//Initializing the class and variables. (Note, This class's specific init should never be called)
		
	}
	
	public void render(Graphics g){	//Renders whatever is in this function
		
	}
	
	public void update(){			//Where the algorithms are repeatedly calculated and applied
		
	}
	
	//Getters for coordinates of this sprite
	public float getX(){ return x; }
	public float getY(){ return y; }
}
