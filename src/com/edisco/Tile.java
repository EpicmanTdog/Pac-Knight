package com.edisco;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import com.edisco.Knight.State;

public class Tile {	//This class takes a look at an outdated and bugged system. It is not used anywhere.
	
	enum tileInfo{FREE, BLOCKED, GHOST, PELLET, ENERGIZER, CHAR};
	tileInfo info = tileInfo.FREE;	//Current Info
	tileInfo def = tileInfo.FREE;	//Default Info
	
	boolean draw = true;
	
	float x, y, x2, y2;
	
	public Tile(float x, float y, tileInfo info) {
		this.x = x;
		this.y = y;
		this.x2 = x + 8;
		this.y2 = y + 8;
		this.def = info;
		this.info = info;
	}
	
	public void drawTile(Graphics g){
		if(info == tileInfo.FREE){
			g.setColor(new Color(200, 200, 200, 100));
		} else if(info == tileInfo.BLOCKED) {
			g.setColor(new Color(10, 10, 200, 100));
		} else if(info == tileInfo.GHOST) {
			g.setColor(new Color(255, 10, 10, 100));
		} else if(info == tileInfo.PELLET) {
			g.setColor(new Color(200, 200, 10, 100));
		} else if(info == tileInfo.ENERGIZER) {
			g.setColor(new Color(200, 100, 10, 100));
		} else if(info == tileInfo.CHAR) {
			g.setColor(new Color(10, 255, 10, 100));
		}
		
		g.fillRect(x, y, 8, 8);
		
		g.setColor(Color.white);
	};
	
	public void setDrawTiles(boolean draw){
		if(draw == true){
			this.draw = true;
		} else if(draw == false){
			this.draw = false;
		} else {
			System.out.println("WARNING: setDrawTiles() has a wrong parameter!");
		}
	}
	                                                           
	public boolean getDrawTiles(){                             
		return draw;                                           
	}                                                          
	                                                           
	public tileInfo getInfo(){                                 
		return info;                                           
	}                                                          
	                                                           
	public void setInfo(tileInfo info){                        
		this.info = info;                                      
	}                                                          
	
	public void checkState(){                                  
		if(Adventure.knight.centerX >= x && Adventure.knight.centerX <= x2 && Adventure.knight.centerY >= y && Adventure.knight.centerY <= y2){
			setInfo(tileInfo.CHAR);
			if(def == tileInfo.PELLET || def == tileInfo.ENERGIZER){
				def = tileInfo.FREE;
			}
		} else {
			info = def;
		}
	}
	
	public void isNextTileBlocked(Knight.State direction){
		if(direction == State.RIGHT){
			
		}
	}
	
}                                                  
                                                   