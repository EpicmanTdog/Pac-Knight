package com.edisco;

import java.util.Random;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.geom.Rectangle;

public class Knight extends Sprite{	//The entire Knight Class
	
	SpriteSheet chars; //Spritesheet that the characters come from (including the ghosts)
	
	enum State{UP, DOWN, LEFT, RIGHT, DEATH}; //Animation States
	State state = State.RIGHT;				  //The Knight starts going right
	State desiredState = State.RIGHT;		  //Just in case
	
	float x; 				//Topleft x of sprite
	float y; 				//topleft y of sprite
	float speed = 1.0f; 	//speed of movement
	float ludispeed = 3.0f; //ludicrious speed
	float x2; 				//midtopleft x of collision box
	float y2; 				//midtopleft y of collision box
	float centerX;			//Center x of sprite
	float centerY;			//Center y of sprite
	
	//The Hero (the player) can choose between 3 different characters to play. They play identically, but the sprites are different.
	Animation knightUp, knightDown, knightLeft, knightRight, knightDeath;	//Knight's animations
	Animation archerUp, archerDown, archerLeft, archerRight, archerDeath; 	//Archer's animations
	Animation wizardUp, wizardDown, wizardLeft, wizardRight, wizardDeath; 	//Wizard's animations
	
	int delta = 300;	//The Delta for animations
	
	//Collision rectangles
	Rectangle colbox;
	
	Rectangle rightColbox;
	Rectangle leftColbox;
	Rectangle upColbox;
	Rectangle downColbox;
	Rectangle midColbox;
	
	//Sounds
	Sound coin;			//The sound for when the hero collects coins
	Sound energizer;	//The sound for when the energizer activates
	
	//Randomizer
	Random rand = new Random();
	
	//Tracks if the hero (and the ghosts in other classes) should be energized
	boolean energized = false;		//The boolean to track it
	Timer enerTimer = new Timer();	//The timer that keeps it going for 8 seconds
	
	//Scoring
	int score = 0;			//The Score
	int lifeScore = 0;		//A stored score that later applies in a formula to track when the Hero should receive an extra life
	
	public void init(){ //Initializing the class and variables. 
		try{ 			//Initializing Spritesheet
			chars = new SpriteSheet("specs/assets/fantasyspritesheet.png", 16, 16);
		} catch(SlickException e){
			e.printStackTrace();
		}
		
		//The position of the Hero
		x = 226f;
		y = 136f;
		
		/*
		 * The animations for the sprites
		 * Each animation's name follows this pattern:
		 * k = Knight
		 * a = Archer
		 * w = Wizard
		 * followed by:
		 * ri = Right
		 * li = Left
		 * ui = Up
		 * di = Down
		 * dd = Death
		 */
		Image[] kri = {chars.getSprite(0, 0), chars.getSprite(1, 0)};
		knightRight = new Animation(kri, delta);
		knightRight.setPingPong(true);
		
		Image[] kli = {chars.getSprite(0, 1), chars.getSprite(1, 1)};
		knightLeft = new Animation(kli, delta);
		knightLeft.setPingPong(true);
		
		Image[] kui = {chars.getSprite(0, 2), chars.getSprite(1, 2)};
		knightUp = new Animation(kui, delta);
		knightUp.setPingPong(true);
		
		Image[] kdi = {chars.getSprite(0, 3), chars.getSprite(1, 3)};
		knightDown = new Animation(kdi, delta);
		knightDown.setPingPong(true);
		
		Image[] kdd = {chars.getSprite(2, 0), chars.getSprite(3, 0), chars.getSprite(4, 0), chars.getSprite(5, 0), chars.getSprite(6, 0), chars.getSprite(7, 0), chars.getSprite(8, 0), chars.getSprite(9, 0), chars.getSprite(10, 0), chars.getSprite(11, 0), chars.getSprite(12, 0), chars.getSprite(13, 0)};
		knightDeath = new Animation(kdd, delta);
		
		Image[] ari = {chars.getSprite(0, 4), chars.getSprite(1, 4)};
		archerRight = new Animation(ari, delta);
		archerRight.setPingPong(true);
		
		Image[] ali = {chars.getSprite(0, 5), chars.getSprite(1, 5)};
		archerLeft = new Animation(ali, delta);
		archerLeft.setPingPong(true);
		
		Image[] aui = {chars.getSprite(0, 6), chars.getSprite(1, 6)};
		archerUp = new Animation(aui, delta);
		archerUp.setPingPong(true);
		
		Image[] adi = {chars.getSprite(0, 7), chars.getSprite(1, 7)};
		archerDown = new Animation(adi, delta);
		archerDown.setPingPong(true);
		
		Image[] add = {chars.getSprite(3, 4), chars.getSprite(4, 4), chars.getSprite(5, 4), chars.getSprite(6, 4), chars.getSprite(7, 4), chars.getSprite(8, 4), chars.getSprite(9, 4), chars.getSprite(10, 4), chars.getSprite(11, 4), chars.getSprite(12, 4), chars.getSprite(13, 4)};
		archerDeath = new Animation(add, delta);
		
		Image[] wri = {chars.getSprite(0, 8), chars.getSprite(1, 8)};
		wizardRight = new Animation(wri, delta);
		wizardRight.setPingPong(true);
		
		Image[] wli = {chars.getSprite(0, 9), chars.getSprite(1, 9)};
		wizardLeft = new Animation(wli, delta);
		wizardLeft.setPingPong(true);
		
		Image[] wdi = {chars.getSprite(0, 10), chars.getSprite(1, 10)};
		wizardDown = new Animation(wdi, delta);
		wizardDown.setPingPong(true);
		
		Image[] wui = {chars.getSprite(0, 11), chars.getSprite(1, 11)};
		wizardUp = new Animation(wui, delta);
		wizardUp.setPingPong(true);
		
		Image[] wdd = {chars.getSprite(3, 8), chars.getSprite(4, 8), chars.getSprite(5, 8), chars.getSprite(6, 8), chars.getSprite(7, 8), chars.getSprite(8, 8), chars.getSprite(9, 8), chars.getSprite(10, 8), chars.getSprite(11, 8), chars.getSprite(12, 8), chars.getSprite(13, 8)};
		wizardDeath = new Animation(wdd, delta);
		
		//Declaring Collision Boxes (Colboxes)
		colbox = new Rectangle(x2, y2, 1, 1);
		
		leftColbox = new Rectangle(x, y, 1, 1);
		rightColbox = new Rectangle(x, y, 1, 1);
		upColbox = new Rectangle(x, y, 1, 1);
		downColbox = new Rectangle(x, y, 1, 1);
		midColbox = new Rectangle(x, y, 1, 1);
		
		try{	//Initializing sounds
			coin = new Sound("/specs/audio/coins/coin1.ogg");
			energizer = new Sound("/specs/audio/fantasy_energizer.ogg");
		} catch(SlickException e){
			e.printStackTrace();
		}
		
		enerTimer.set(0);	//Energizer Timer
		score = 0;			//Initializing the score
		
	}
	
	public void render(Graphics g){
		//Putting the Animations to use
		if(Options.getChar() == 0){		//Used for Knight
			if(state == State.RIGHT){
				knightRight.draw(x, y);
			} else if(state == State.LEFT){
				knightLeft.draw(x, y);
			} else if(state == State.UP){
				knightUp.draw(x, y);
			} else if(state == State.DOWN){
				knightDown.draw(x, y);
			} else if(state == State.DEATH){
				knightDeath.draw(x, y);
			}
		} else if(Options.getChar() == 1){	//Used for Archer
			if(state == State.RIGHT){
				archerRight.draw(x, y);
			} else if(state == State.LEFT){
				archerLeft.draw(x, y);
			} else if(state == State.UP){
				archerUp.draw(x, y);
			} else if(state == State.DOWN){
				archerDown.draw(x, y);
			} else if(state == State.DEATH){
				archerDeath.draw(x, y);
			}
		} else if(Options.getChar() == 2){	//Used for Wizard
			if(state == State.RIGHT){
				wizardRight.draw(x, y);
			} else if(state == State.LEFT){
				wizardLeft.draw(x, y);
			} else if(state == State.UP){
				wizardUp.draw(x, y);
			} else if(state == State.DOWN){
				wizardDown.draw(x, y);
			} else if(state == State.DEATH){
				wizardDeath.draw(x, y);
			}
		}
	}
	
	public void update(){
		
		Timer.tick();	//Updates Timer
		
		GameContainer container = Main.app;	//Initializing Input
		Input input = container.getInput();	
		
		if(state != State.DEATH){	//Allows the character to do things if they aren't dead.
			
			x2 = x + 6;
			y2 = y + 24;
			centerX = x + 8;
			centerY = y + 8;
			
			//Collision box stuff
			leftColbox.setX(x);
			leftColbox.setY(y+1.3f);
			leftColbox.setHeight(13.6f);
			leftColbox.setWidth(1f);
			
			rightColbox.setX(x+15);
			rightColbox.setY(y+1.3f);
			rightColbox.setHeight(13.6f);
			rightColbox.setWidth(1f);
			
			upColbox.setX(x+1.3f);
			upColbox.setY(y);
			upColbox.setWidth(13.6f);
			upColbox.setHeight(1f);
			
			downColbox.setX(x+1.3f);
			downColbox.setY(y+15);
			downColbox.setWidth(13.6f);
			downColbox.setHeight(1f);
			
			midColbox.setX(x2);
			midColbox.setY(y2);
			midColbox.setWidth(4f);
			midColbox.setHeight(4f);
			
			//Changes desired state based on where the joystick is/which d-pad button is pressed
			if(input.isControllerDown(1)){
				desiredState = State.DOWN;
			}
			if(input.isControllerUp(1)){
				desiredState = State.UP;
			}
			if(input.isControllerRight(1)){
				desiredState = State.RIGHT;
			}
			if(input.isControllerLeft(1)){
				desiredState = State.LEFT;
			}
			
			if(desiredState != state){	//desiredState Verification. If possible, it switches the direction.
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
			
			//Decides where the character goes based on state and possibility
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
			
			if(enerTimer.getTime() > 0 && energized == true){	//If the timer is out, change from energized to normal
				energized = false;
			}
			
			checkTeleRight();	//Checks the Right Tunnel for characters
			checkTeleLeft();	//Same but for Left Tunnel
			checkPellet();		//Checks for pellet (coin) collection
			checkEner();		//Checks for Energizer Collection
			checkGhost();		//Checks for collisions with Ghosts, which promptly kills the player if true
			
			if(score >= lifeScore + 10000){
				lifeScore += 10000;
				Menu.lives++;
			}
		}	
	}	
	
	//Simple getters for coords
	public float getX(){ return centerX; }
	public float getY(){ return centerY; }
	
	public boolean checkIntersect(){	//Checks for Intersections with Walls
		
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
			if(downColbox.intersects(Adventure.walls.get(i).position)){
				return true;
			}
		}
		return false;
		
	}
	
	public void checkTeleRight(){	//Checks the right tunnel
		
		if(rightColbox.intersects(Adventure.teles[1].position)){
			this.x = 125f;
			this.y = 112f;
		}
		
	}
	
	public void checkTeleLeft(){	//Checks the left tunnel
		
		if(leftColbox.intersects(Adventure.teles[0].position)){
			this.x = 320f;
			this.y = 112f;
		}
		
	}
	
	public void checkPellet(){		//Checks the pellets for collection
		
		for(int i = 0; i < Adventure.pellets.size(); i++){
			if(midColbox.intersects(Adventure.pellets.get(i).position)){
				Adventure.pellets.remove(i);
				coin.play();
				score += 10;
			}
		}
		
	}
	
	public void checkEner(){		//Checks the Energizers
		for(int i = 0; i < Adventure.energizers.size(); i++){
			if(midColbox.intersects(Adventure.energizers.get(i).position)){
				Adventure.energizers.remove(i);
				energizer.play();
				energized = true;
				enerTimer.set(-8.0f);
				Adventure.necro.energized = true;
				Adventure.necro.enerTimer.set(-8.0f);
				Adventure.skele.energized = true;
				Adventure.skele.enerTimer.set(-8.0f);
				Adventure.wraith.energized = true;
				Adventure.wraith.enerTimer.set(-8.0f);
				Adventure.wisp.energized = true;
				Adventure.wisp.enerTimer.set(-8.0f);
				score += 50;
			}
		}
	}
	
	public void checkGhost(){		//Checks collisions with Ghosts
		if(midColbox.intersects(Adventure.necro.midColbox)){
			if(!energized) {
				state = State.DEATH;
				Adventure.startTimer.set(-2f);
				Menu.lives -= 1;
				if(Menu.lives < 0){
					Menu.lives = 0;
				}
			} else {
				Adventure.necro.state = Necromancer.State.DEATH;
				score+=500;
			}
		}
		if(Adventure.skeleInited){
			if(midColbox.intersects(Adventure.skele.midColbox)){
				if(!energized) {
					state = State.DEATH;
					Adventure.startTimer.set(-2f);
					Menu.lives -= 1;
					if(Menu.lives < 0){
						Menu.lives = 0;
					}
				} else {
					Adventure.skele.state = Skeleton.State.DEATH;
					score+=500;
				}
			}
		}
		if(Adventure.wraithInited){
			if(midColbox.intersects(Adventure.wraith.midColbox)){
				if(!energized) {
					state = State.DEATH;
					Adventure.startTimer.set(-2f);
					Menu.lives -= 1;
					if(Menu.lives < 0){
						Menu.lives = 0;
					}
				} else {
					Adventure.wraith.state = Wraith.State.DEATH;
					score+=500;
				}
			}
		}
		if(Adventure.wispInited){
			if(midColbox.intersects(Adventure.wisp.midColbox)){
				if(!energized) {
					state = State.DEATH;
					Adventure.startTimer.set(-2f);
					Menu.lives -= 1;
					if(Menu.lives < 0){
						Menu.lives = 0;
					}
				} else {
					Adventure.wisp.state = Wisp.State.DEATH;
					score+=500;
				}
			}
		}
	}
	
}
