package com.edisco;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

public class Pellet {	//The "coins"
	
	//Coordinates
	int x;
	int y;
	
	//The Sprite of the coin
	Image image;
	
	//The rectangle that collides with the Hero
	Rectangle position;
	
	public Pellet(int x, int y) {					//The constructor
		this.position = new Rectangle(x, y, 8, 8);	//Makes the new collision box
		this.x = x;									//Places it at the specified X coord
		this.y = y;									//Places it at the specified Y coord
		
		//Makes the sprite
		try{
			 image = new Image("specs/assets/fantasypellet.png");
			 image.setFilter(Image.FILTER_NEAREST);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public void draw(){						//Draws the sprite
		image.draw(this.x-2, this.y-20);
	}
}