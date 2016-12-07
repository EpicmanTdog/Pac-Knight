package com.edisco;

import org.newdawn.slick.geom.Rectangle;

public class Wall {	//Creates a "wall" that characters cannot pass through
	
	Rectangle position;	//The collision box that blocks characters by their inherent "checkers"
	
	public Wall(int x, int y, int width, int height) {	//The constructor, which takes coordinates, width, and height into account as required parameters
		this.position = new Rectangle(x, y, width, height);	//Creates the box
	}
	
	public Rectangle getColMask(){	//Returns the collision box
		return position;
	}
	
}
