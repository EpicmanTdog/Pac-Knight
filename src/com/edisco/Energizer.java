package com.edisco;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Energizer {
	
	int x;			//The X Coordinate of this object
	int y;			//The Y Coordinate of this object
	Image image;	//The sprite
	
	Rectangle position;	//The collision box of the object. Applies to pellets and walls too.
	
	public Energizer(int x, int y) {					//The constructor, which takes parameters for where to place it
		this.position = new Rectangle(x, y, 8, 8);		//Instantiates the collision box
		this.x = x;										//Puts it at the X stated
		this.y = y;										//Puts it at the Y stated
		
		try{	//Creates the Sprite
			 image = new Image("specs/assets/fantasyenergizer.png");
			 image.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(){		//Draws the Energizer at it's X and Y coordinates. Also takes a .8 scale to fit into the maze.
		image.draw(this.x-2, this.y-20, .8f);	//I'm unsure why, but the function draws it 2 pixels to the right and 20 below where the actual energizer is
	}

}
