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

public class Skeleton extends Ghost {	//Refer to "Necromancer.java" for comments explaining everything
	
	SpriteSheet chars; //Spritesheet
	
	enum State{UP, DOWN, LEFT, RIGHT, DEATH}; //Animation States
	State state = State.LEFT;
	State desiredState = State.LEFT;
	
	float x; //Topleft x of sprite
	float y;//topleft y of sprite
	float speed = 0.8f; //speed of movement
	float ludispeed = 3.0f; //ludicrious speed
	float x2; //midtopleft x of collision box
	float y2; //midtopleft y of collision box
	float centerX; //Center x of sprite
	float centerY; //Center y of sprite
	float targetX;
	float targetY;
	
	boolean energized = false;
	
	Timer enerTimer = new Timer();
	
	Animation skeletonUp, skeletonDown, skeletonLeft, skeletonRight;	  	//Skeleton
	Animation eSkeletonUp, eSkeletonDown, eSkeletonLeft, eSkeletonRight;
	int delta = 300;
	int mapDelta = 16;
	int tileId;
	String tileValue;
	
	//Collision rectangle
	Rectangle colbox;
	
	Rectangle rightColbox;
	Rectangle leftColbox;
	Rectangle upColbox;
	Rectangle downColbox;
	Rectangle midColbox;
	
	//Target Rectangle
	Rectangle targRect;
	
	//Changing
	boolean canChange = true;
	Timer canChangeTimer = new Timer();
	
	//ScatterBrain
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
		
		x = 223f;
		y = 87.2f;
		
		//Defining Animations
		Image[] sri = {chars.getSprite(1, 12), chars.getSprite(2, 12)};
		skeletonRight = new Animation(sri, delta);
		skeletonRight.setPingPong(true);
		
		Image[] sli = {chars.getSprite(1, 13), chars.getSprite(2, 13)};
		skeletonLeft = new Animation(sli, delta);
		skeletonLeft.setPingPong(true);
		
		Image[] sdi = {chars.getSprite(1, 14), chars.getSprite(2, 14)};
		skeletonDown = new Animation(sdi, delta);
		skeletonDown.setPingPong(true);
		
		Image[] sui = {chars.getSprite(1, 15), chars.getSprite(2, 15)};
		skeletonUp = new Animation(sui, delta);
		skeletonUp.setPingPong(true);
		
		Image[] esri = {chars.getSprite(1, 16), chars.getSprite(2, 16)};
		eSkeletonRight = new Animation(esri, delta);
		eSkeletonRight.setPingPong(true);
		
		Image[] esli = {chars.getSprite(1, 17), chars.getSprite(2, 17)};
		eSkeletonLeft = new Animation(esli, delta);
		eSkeletonLeft.setPingPong(true);
		
		Image[] esdi = {chars.getSprite(1, 18), chars.getSprite(2, 18)};
		eSkeletonDown = new Animation(esdi, delta);
		eSkeletonDown.setPingPong(true);
		
		Image[] esui = {chars.getSprite(1, 19), chars.getSprite(2, 19)};
		eSkeletonUp = new Animation(esui, delta);
		eSkeletonUp.setPingPong(true);
		
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
			skeletonRight.draw(x, y);
		} else if(state == State.LEFT && !energized){
			skeletonLeft.draw(x, y);
		} else if(state == State.UP && !energized){
			skeletonUp.draw(x, y);
		} else if(state == State.DOWN && !energized){
			skeletonDown.draw(x, y);
		} else if(state == State.RIGHT && energized){
			eSkeletonRight.draw(x, y);
		} else if(state == State.LEFT && energized){
			eSkeletonLeft.draw(x, y);
		} else if(state == State.DOWN && energized){
			eSkeletonDown.draw(x, y);
		} else if(state == State.UP && energized){
			eSkeletonUp.draw(x, y);
		} else {
			
		}
		
	}
	
	public void update(){
		
		Timer.tick();	//Ticks away at the Timer
		
		checkTeleRight();
		checkTeleLeft();
		checkChoiceDest();
		
		if(canChangeTimer.getTime() > 1f){
			tookDifPath = false;
		}
		
		if(Extras.getScatterbrain() || energized){
			Scatterbrain = true;
		} else {
			Scatterbrain = false;
		}
		
		if(state != State.DEATH){	//Allows it to do stuff if not dead
			if(!Scatterbrain){		//If the Extras option "Scatterbrain" was turned off
				if(dirTimeout.getTime() > 1f){
					if(Adventure.knight.state == Knight.State.LEFT){
						targetX = Adventure.knight.getX() - 32;
						targetY = Adventure.knight.getY();
					} else if(Adventure.knight.state == Knight.State.RIGHT){
						targetX = Adventure.knight.getX() + 32;
						targetY = Adventure.knight.getY();
					} else if(Adventure.knight.state == Knight.State.UP){
						targetX = Adventure.knight.getX();
						targetY = Adventure.knight.getY() - 32;
					} else if(Adventure.knight.state == Knight.State.DOWN){
						targetX = Adventure.knight.getX();
						targetY = Adventure.knight.getY() + 32;
					}
				}
				
				targRect.setX(centerX);
				targRect.setY(centerY);
				targRect.setHeight(targetY - centerY);
				targRect.setWidth(targetX - centerX);
				
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
				
				if(state == State.LEFT){
					colbox.setX(leftColbox.getX());
					colbox.setY(leftColbox.getY());
					colbox.setHeight(leftColbox.getHeight());
					colbox.setWidth(leftColbox.getWidth());
					
					if(checkIntersect() == false){
						if(!Extras.getSpeedyMode()){
							x -= speed;
						} else {
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
				
				if(checkChoiceDest() && dirTimeout.getTime() > 0 && !tookDifPath) {
					
					//dirTimeout.set(timerDelta);
					
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
					} else {
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
				
//				checkTeleRight();
//				checkTeleLeft();
//				checkChoiceDest();
				rightAllowed = true;
				leftAllowed = true;
				upAllowed = true;
				downAllowed = true;
				
				

			}
		} else {
			
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
			
			if(scatterTimer.getTime() > 0){
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
			
			if(enerTimer.getTime() > 0f && energized == true){
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
