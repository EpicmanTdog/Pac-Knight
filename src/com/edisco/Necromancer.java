package com.edisco;

import java.util.Random;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

import com.edisco.Knight.State;

public class Necromancer extends Ghost {
	
	SpriteSheet chars; //Spritesheet
	
	enum State{UP, DOWN, LEFT, RIGHT, DEATH}; //Animation States
	State state = State.LEFT;
	State desiredState = State.LEFT;
	
	float x; 				//Topleft x of sprite
	float y; 				//topleft y of sprite
	float speed = 0.8f; 	//speed of movement
	float ludispeed = 3.0f; //ludicrious speed
	float x2; 				//midtopleft x of collision box
	float y2; 				//midtopleft y of collision box
	float centerX; 			//Center x of sprite
	float centerY; 			//Center y of sprite
	float targetX;			//The X coord that the ghost is targetting
	float targetY;			//The Y coord that the ghost is targetting
	
	boolean energized = false;		//A boolean for when the ghost is energized
	Timer enerTimer = new Timer();	//The energizer timer
	
	//Animations
	Animation necroUp, necroDown, necroLeft, necroRight;		//Normal
	Animation eNecroUp, eNecroDown, eNecroLeft, eNecroRight;	//Energized
	int delta = 300;											//The Delta that the animations move at
	
	//Collision rectangles
	Rectangle colbox;
	
	Rectangle rightColbox;
	Rectangle leftColbox;
	Rectangle upColbox;
	Rectangle downColbox;
	Rectangle midColbox;
	
	//A target rectangle to help with directional decisions (More on this in Update())
	Rectangle targRect;
	
	//Helps with decision making
	boolean canChange = true;
	Timer canChangeTimer = new Timer();
	
	//Checks if this ghost should be scatterbrained
	boolean Scatterbrain = false;
	Timer scatterTimer = new Timer();
	
	//Choice variables (Relatively self explanatory)
	static boolean rightAllowed, leftAllowed, upAllowed, downAllowed;
	static boolean[] allowed = {rightAllowed, leftAllowed, upAllowed, downAllowed};
	int choices = 0;
	int randInt = 0;
	
	Random rand = new Random();	//Randomizer variable
	
	Timer dirTimeout = new Timer();	//Directional Timeouts
	float timerDelta = -.1f;
	boolean tookDifPath = false;
	
	public void init(){ //Initializing the class and variables. 
		try{ 			//Initializing Spritesheet
			chars = new SpriteSheet("specs/assets/fantasyspritesheet.png", 16, 16);
		} catch(SlickException e){
			e.printStackTrace();
		}
		
		//Coordinates of the ghost
		x = 223f;
		y = 87.2f;
		
		//Defining Animations (if it starts with a lowercase e, it means this shows when the ghost is energized, otherwise names are self-explanatory)
		Image[] nri = {chars.getSprite(0, 12)};
		necroRight = new Animation(nri, delta);
		
		Image[] nli = {chars.getSprite(0, 13)};
		necroLeft = new Animation(nli, delta);
		
		Image[] ndi = {chars.getSprite(0, 14)};
		necroDown = new Animation(ndi, delta);
		
		Image[] nui = {chars.getSprite(0, 15)};
		necroUp = new Animation(nui, delta);
		
		Image[] enri = {chars.getSprite(0, 16)};
		eNecroRight = new Animation(enri, delta);
		
		Image[] enli = {chars.getSprite(0, 17)};
		eNecroLeft = new Animation(enli, delta);
		
		Image[] endi = {chars.getSprite(0, 18)};
		eNecroDown = new Animation(endi, delta);
		
		Image[] enui = {chars.getSprite(0, 19)};
		eNecroUp = new Animation(enui, delta);
		
		//Collision box stuff
		colbox = new Rectangle(x2, y2, 1, 1);
		
		rightColbox = new Rectangle(x, y, 1, 1);
		leftColbox = new Rectangle(x, y, 1, 1);
		downColbox = new Rectangle(x, y, 1, 1);
		upColbox = new Rectangle(x, y, 1, 1);
		midColbox = new Rectangle(x, y, 1, 1);
		
		enerTimer.set(0);	//Energizer Timer
		
		targRect = new Rectangle(x, y, 2, 2);	//Rectangle between Necromancer and it's Target Coordinate
		
		//Declaring Directional Booleans
		rightAllowed = false;
		leftAllowed = false;
		upAllowed = false;
		downAllowed = false;
		
		dirTimeout.set(0);	//Declaring Timeouts
		canChangeTimer.set(0);
		scatterTimer.set(0);
		
	}
	
	public void render(Graphics g){
		//Putting the Animations to use
		if(state == State.RIGHT && !energized){
			necroRight.draw(x, y);
		} else if(state == State.LEFT && !energized){
			necroLeft.draw(x, y);
		} else if(state == State.UP && !energized){
			necroUp.draw(x, y);
		} else if(state == State.DOWN && !energized){
			necroDown.draw(x, y);
		} else if(state == State.RIGHT && energized){
			eNecroRight.draw(x, y);
		} else if(state == State.LEFT && energized){
			eNecroLeft.draw(x, y);
		} else if(state == State.DOWN && energized){
			eNecroDown.draw(x, y);
		} else if(state == State.UP && energized){
			eNecroUp.draw(x, y);
		} else {
			
		}
		
	}
	
	public void update(){
		
		Timer.tick();	//Ticks away at the Timer
		
		//Checks for collisions on certain things
		checkTeleRight();
		checkTeleLeft();
		checkChoiceDest();
		
		if(canChangeTimer.getTime() > 1f){	//Keeps the ghost from staying in one place if it can't figure out where it's going
			tookDifPath = false;
		}
		
		if(Extras.getScatterbrain() || energized){	//Makes the ghost scatterbrained if it's energized or if the Extras option was turned on
			Scatterbrain = true;
		} else {
			Scatterbrain = false;
		}
		
		if(state != State.DEATH){	//Allows it to do stuff if not dead
			if(!Scatterbrain){		//If the Extras option "Scatterbrain" was turned off
				
				if(dirTimeout.getTime() > 1f){			//Targetting is glitch if it starts immediately
					targetX = Adventure.knight.centerX;
					targetY = Adventure.knight.centerY;
				}
				
				//Sets up the target rectangle
				targRect.setX(centerX);
				targRect.setY(centerY);
				targRect.setHeight(Adventure.knight.centerY - centerY);
				targRect.setWidth(Adventure.knight.centerX - centerX);
				
				//Coord helping
				x2 = x + 6;
				y2 = y + 24;
				centerX = x + 8;
				centerY = y + 8;
				
				//Updating the collision boxes with movement
				leftColbox.setX(x+.5f);
				leftColbox.setY(y+1.3f);
				leftColbox.setHeight(13.5f);
				leftColbox.setWidth(1f);
				
				rightColbox.setX(x+15);
				rightColbox.setY(y+1.3f);
				rightColbox.setHeight(13.5f);
				rightColbox.setWidth(1f);
				
				upColbox.setX(x+1.3f);
				upColbox.setY(y+.5f);
				upColbox.setWidth(13.5f);
				upColbox.setHeight(1f);
				
				downColbox.setX(x+1.3f);
				downColbox.setY(y+15);
				downColbox.setWidth(13.5f);
				downColbox.setHeight(1f);
				
				midColbox.setX(x2);
				midColbox.setY(y2);
				midColbox.setWidth(4f);
				midColbox.setHeight(4f);
				
				if(state != desiredState){	//If the ghost wants to go a new direction and it becomes available, let it switch to that direction. Otherwise it will keep going until it does become available
					if(desiredState == State.RIGHT && !checkRightIntersect()){
						state = State.RIGHT;
						canChangeTimer.set(0);
					} else if(desiredState == State.LEFT && !checkLeftIntersect()){
						state = State.LEFT;
						canChangeTimer.set(0);
					} else if(desiredState == State.UP && !checkUpIntersect()){
						state = State.UP;
						canChangeTimer.set(0);
					} else if(desiredState == State.DOWN && !checkDownIntersect()){
						state = State.DOWN;
						canChangeTimer.set(0);
					} 
				}
				
				//Updates the position of the ghost based on direction and availability
				if(state == State.LEFT){
					colbox.setX(leftColbox.getX());
					colbox.setY(leftColbox.getY());
					colbox.setHeight(leftColbox.getHeight());
					colbox.setWidth(leftColbox.getWidth());
					
					if(checkIntersect() == false){
						if(!Extras.getSpeedyMode()){	//If Ludicrous speed in the Extras Screen was turned off (Extras.java)
							x -= speed;
						} else {						//if it was turned on
							x -= ludispeed;	
						}
					}
					
				} else if(state == State.RIGHT){
					colbox.setX(rightColbox.getX());
					colbox.setY(rightColbox.getY());
					colbox.setHeight(rightColbox.getHeight());
					colbox.setWidth(rightColbox.getWidth());
					
					if(checkIntersect() == false){
						if(!Extras.getSpeedyMode()){
							x += speed;
						} else {
							x += ludispeed;
						}
					}
				} else if(state == State.UP){
					colbox.setX(upColbox.getX());
					colbox.setY(upColbox.getY());
					colbox.setHeight(upColbox.getHeight());
					colbox.setWidth(upColbox.getWidth());
					
					if(checkIntersect() == false){
						if(!Extras.getSpeedyMode()){
							y -= speed;
						} else {
							y -= ludispeed;
						}
					}
				} else if(state == State.DOWN){
					colbox.setX(downColbox.getX());
					colbox.setY(downColbox.getY());
					colbox.setHeight(downColbox.getHeight());
					colbox.setWidth(downColbox.getWidth());
					
					if(checkIntersect() == false){
						if(!Extras.getSpeedyMode()){
							y += speed;
						} else {
							y += ludispeed;
						}
					}
				}
				
				if(checkChoiceDest() && dirTimeout.getTime() > 0 && !tookDifPath) {	//The decision making process of the ghost. It will go whichever way takes longest to get to it's target.
					
					//dirTimeout.set(timerDelta);
					
					//if the ghost goes a different direction than where it wants to go, it will not change it's direction again for another second. This is to prevent it from getting stuck.
					if(targetX < centerX && targetY < centerY && targRect.getHeight() > targRect.getWidth()){
						if(leftAllowed){
							desiredState = State.LEFT;
						} else if(upAllowed) {
							desiredState = State.UP;
						} else if (downAllowed){
							desiredState = State.DOWN;
							tookDifPath = true;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
							tookDifPath = true;
						}
					} else if(targetX < centerX && targetY < centerY && targRect.getHeight() < targRect.getWidth()){
						if(upAllowed){
							desiredState = State.UP;
						} else if(leftAllowed){
							desiredState = State.LEFT;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
							tookDifPath = true;
						} else if(downAllowed){
							desiredState = State.DOWN;
							tookDifPath = true;
						}
					} else if(targetX < centerX && targetY > centerY && Math.abs(targRect.getHeight()) < Math.abs(targRect.getWidth())){
						if(leftAllowed){
							desiredState = State.LEFT;
						} else if(downAllowed){
							desiredState = State.DOWN;
						} else if(upAllowed){
							desiredState = State.UP;
							tookDifPath = true;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
							tookDifPath = true;
						}
					} else if(targetX < centerX && targetY > centerY && Math.abs(targRect.getHeight()) > Math.abs(targRect.getWidth())){
						if(downAllowed){
							desiredState = State.DOWN;
						} else if(leftAllowed){
							desiredState = State.LEFT;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
							tookDifPath = true;
						} else if(upAllowed){
							desiredState = State.UP;
							tookDifPath = true;
						}
					} else if(targetX > centerX && targetY > centerY && targRect.getHeight() < targRect.getWidth()){
						if(rightAllowed){
							desiredState = State.RIGHT;
						} else if(downAllowed){
							desiredState = State.DOWN;
						} else if(upAllowed){
							desiredState = State.UP;
							tookDifPath = true;
						} else if(leftAllowed){
							desiredState = State.LEFT;
							tookDifPath = true;
						}
					} else if(targetX > centerX && targetY > centerY && targRect.getHeight() > targRect.getWidth()){
						if(downAllowed){
							desiredState = State.DOWN;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
						} else if(leftAllowed){
							desiredState = State.LEFT;
							tookDifPath = true;
						} else if(upAllowed){
							desiredState = State.UP;
							tookDifPath = true;
						}
					} else if(targetX > centerX && targetY < centerY && Math.abs(targRect.getHeight()) > Math.abs(targRect.getWidth())){
						if(upAllowed){
							desiredState = State.UP;
						} else if(rightAllowed){
							desiredState = State.RIGHT;
						} else if(leftAllowed){
							desiredState = State.LEFT;
							tookDifPath = true;
						} else if(downAllowed){
							desiredState = State.DOWN;
							tookDifPath = true;
						}
					} else if(targetX > centerX && targetY < centerY && Math.abs(targRect.getHeight()) < Math.abs(targRect.getWidth())){
						if(rightAllowed){
							desiredState = State.RIGHT;
						} else if(upAllowed){
							desiredState = State.UP;
						} else if(downAllowed){
							desiredState = State.DOWN;
							tookDifPath = true;
						} else if(leftAllowed){
							desiredState = State.LEFT;
							tookDifPath = true;
						}
					} else if(Math.abs(targRect.getHeight()) < 2 || Math.abs(targRect.getWidth()) < 2){
						if(rightAllowed){
							desiredState = State.RIGHT;
							dirTimeout.set(timerDelta);
						} else if(leftAllowed){
							desiredState = State.LEFT;
							dirTimeout.set(timerDelta);
						} else if(upAllowed){
							desiredState = State.UP;
							dirTimeout.set(timerDelta);
						} else if(downAllowed){
							desiredState = State.DOWN;
							dirTimeout.set(timerDelta);
						}
					} else if(rightAllowed){
						desiredState = State.RIGHT;
						dirTimeout.set(timerDelta);
					} else if(leftAllowed){
						desiredState = State.LEFT;
						dirTimeout.set(timerDelta);
					} else if(upAllowed){
						desiredState = State.UP;
						dirTimeout.set(timerDelta);
					} else if(downAllowed){
						desiredState = State.DOWN;
						dirTimeout.set(timerDelta);
					} else {	//Last resort, almost never happens however
						int trueCount = 0;
						for(int i = 0; i < allowed.length; i++){
							if(allowed[i] == true){
								trueCount++;
							}
						}
						
						randInt = rand.nextInt(trueCount);
						
						if(randInt == 1 && downAllowed){
							desiredState = State.DOWN;
						} else if(randInt == 2 && upAllowed){
							desiredState = State.UP;
						} else if(randInt == 3 && leftAllowed){
							desiredState = State.LEFT;
						} else if(randInt == 4 && rightAllowed){
							desiredState = State.RIGHT;
						}
					}
				
				if(enerTimer.getTime() > 0f && energized == true){
					energized = false;
				}
				
				//Resets booleans, otherwise it will consistently think it can't go anywhere
				rightAllowed = true;
				leftAllowed = true;
				upAllowed = true;
				downAllowed = true;
				
				

			}
		} else {	//In case Scatterbrain was turned on in the Extras menu, or it's energized
			
			x2 = x + 6;
			y2 = y + 24;
			centerX = x + 8;
			centerY = y + 8;
			
			leftColbox.setX(x+.5f);
			leftColbox.setY(y+1.3f);
			leftColbox.setHeight(13.5f);
			leftColbox.setWidth(1f);
			
			rightColbox.setX(x+15);
			rightColbox.setY(y+1.3f);
			rightColbox.setHeight(13.5f);
			rightColbox.setWidth(1f);
			
			upColbox.setX(x+1.3f);
			upColbox.setY(y+.5f);
			upColbox.setWidth(13.5f);
			upColbox.setHeight(1f);
			
			downColbox.setX(x+1.3f);
			downColbox.setY(y+15);
			downColbox.setWidth(13.5f);
			downColbox.setHeight(1f);
			
			midColbox.setX(x2);
			midColbox.setY(y2);
			midColbox.setWidth(4f);
			midColbox.setHeight(4f);
			
			if(state != desiredState){
				
				if(desiredState == State.RIGHT && !checkRightIntersect()){
					state = State.RIGHT;
				} else if(desiredState == State.LEFT && !checkLeftIntersect()){
					state = State.LEFT;
				} else if(desiredState == State.UP && !checkUpIntersect()){
					state = State.UP;
				} else if(desiredState == State.DOWN && !checkDownIntersect()){
					state = State.DOWN;
				} 
			}
			
			if(state == State.LEFT){
				colbox.setX(leftColbox.getX());
				colbox.setY(leftColbox.getY());
				colbox.setHeight(leftColbox.getHeight());
				colbox.setWidth(leftColbox.getWidth());
				
				if(checkIntersect() == false){
					x -= speed;
				}
				
			} else if(state == State.RIGHT){
				colbox.setX(rightColbox.getX());
				colbox.setY(rightColbox.getY());
				colbox.setHeight(rightColbox.getHeight());
				colbox.setWidth(rightColbox.getWidth());
				
				if(checkIntersect() == false){
					x += speed;
				}
			} else if(state == State.UP){
				colbox.setX(upColbox.getX());
				colbox.setY(upColbox.getY());
				colbox.setHeight(upColbox.getHeight());
				colbox.setWidth(upColbox.getWidth());
				
				if(checkIntersect() == false){
					y -= speed;
				}
			} else if(state == State.DOWN){
				colbox.setX(downColbox.getX());
				colbox.setY(downColbox.getY());
				colbox.setHeight(downColbox.getHeight());
				colbox.setWidth(downColbox.getWidth());
				
				if(checkIntersect() == false){
					y += speed;
				}
			}
			
			if(scatterTimer.getTime() > 0){	//Goes a random direction every .6 seconds
				scatterTimer.set(-.6f);
				checkChoiceDest();
				
				int i = rand.nextInt(3);
				
				if(i == 0){
					desiredState = State.RIGHT;
				} else if(i == 1) {
					desiredState = State.LEFT;
				} else if(i == 2) {
					desiredState = State.DOWN;
				} else if(i == 3) {
					desiredState = State.UP;
				}
				
			}
			
			if(enerTimer.getTime() > 0f && energized == true){	//Marks when the energizer should end
				energized = false;
			}
			
//			checkTeleRight();
//			checkTeleLeft();
//			checkChoiceDest();
			rightAllowed = true;
			leftAllowed = true;
			upAllowed = true;
			downAllowed = true;

			}
		} else {
			x = 223.5f;
			y = 87f;
			
			x2 = x + 6;
			y2 = y + 24;
			centerX = x + 8;
			centerY = y + 8;
			
			leftColbox.setX(x+.5f);
			leftColbox.setY(y+1.3f);
			leftColbox.setHeight(13.5f);
			leftColbox.setWidth(1f);
			
			rightColbox.setX(x+15);
			rightColbox.setY(y+1.3f);
			rightColbox.setHeight(13.5f);
			rightColbox.setWidth(1f);
			
			upColbox.setX(x+1.3f);
			upColbox.setY(y+.5f);
			upColbox.setWidth(13.5f);
			upColbox.setHeight(1f);
			
			downColbox.setX(x+1.3f);
			downColbox.setY(y+15);
			downColbox.setWidth(13.5f);
			downColbox.setHeight(1f);
			
			midColbox.setX(x2);
			midColbox.setY(y2);
			midColbox.setWidth(4f);
			midColbox.setHeight(4f);
			
			if(enerTimer.getTime() > 0){
				dirTimeout.set(-1f);
				state = State.UP;
			}
		}
	}
	
	//Below are a bunch of Intersection and directional checkers that keep the ghost from "ghosting" through the walls
	public boolean checkIntersect(){
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(colbox.intersects(Adventure.walls.get(i).position)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkRightIntersect(){
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(rightColbox.intersects(Adventure.walls.get(i).position)){
				return true;
			}
		}
		return false;
		
	}
	
	public boolean checkLeftIntersect(){

		for(int i = 0; i < Adventure.walls.size(); i++){
			if(leftColbox.intersects(Adventure.walls.get(i).position)){
				return true;
			}
		}
		return false;
		
	}
	
	public boolean checkUpIntersect(){
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(upColbox.intersects(Adventure.walls.get(i).position)){
				return true;
			}
		}
		return false;
		
	}
	
	public boolean checkDownIntersect(){
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(downColbox.intersects(Adventure.walls.get(i).position) || downColbox.intersects(Adventure.ghostHome.position)){
				return true;
			}
		}
		return false;
		
	}
	
	public void checkTeleRight(){
		
		if(rightColbox.intersects(Adventure.teles[1].position)){
			this.x = 125f;
			this.y = 112f;
		}
		
	}
	
	public void checkTeleLeft(){
		
		if(leftColbox.intersects(Adventure.teles[0].position)){
			this.x = 320f;
			this.y = 112f;
		}
		
	}
	
	@SuppressWarnings("static-access")
	public boolean checkChoiceDest(){
		
		int choices = -212;
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(rightColbox.intersects(Adventure.walls.get(i).position)){
				this.rightAllowed = false;
			} else {
				choices++;
			}
		}
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(leftColbox.intersects(Adventure.walls.get(i).position)){
				this.leftAllowed = false;
			} else {
				choices++;
			}
		}
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(upColbox.intersects(Adventure.walls.get(i).position)){
				this.upAllowed = false;
			} else {
				choices++;
			}
		}
		
		for(int i = 0; i < Adventure.walls.size(); i++){
			if(downColbox.intersects(Adventure.walls.get(i).position)  || downColbox.intersects(Adventure.ghostHome.position)){
				this.downAllowed = false;
			} else {
				choices++;
			}
		}
		
		this.choices = choices;
		
		return true;
	}
	
	public void chooseRandDir(){
		if(rightAllowed){
			desiredState = State.RIGHT;
		} else if(leftAllowed){
			desiredState = State.LEFT;
		} else if(downAllowed){
			desiredState = State.DOWN;
		} else if(upAllowed){
			desiredState = State.UP;
		}
	}
	
}
