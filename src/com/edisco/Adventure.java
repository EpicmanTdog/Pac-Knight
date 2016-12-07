package com.edisco;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import com.edisco.Necromancer.State;

public class Adventure extends BasicGameState{
	//The Map
	static TiledMap map;
	
	//Map collisions
	static int mapLayer;
	
	//Fonts
	java.awt.Font awtfont;		//Base font to work with. Not used in render context.
	UnicodeFont slickfont;		//Normal font used for anything that isn't selected by the cursor
	UnicodeFont selectedfont;	//The font used for an option selected by the cursor
	
	//Sounds
	Sound firstTheme;		//The theme that plays at the very beginning of the game
	Sound[] coin;			//An array of noises for when a coin is picked up by the Knight
	Sound energized;		//The sound that plays when an energizer is picked up by the Knight
	
	//Timer
	static Timer timeout;	//Just keeps the sound from replaying every tick and destroying your speakers
	
	//Randomizer
	Random rand = new Random();	//A Randomizer variable. A luxury more than anything, as typing out Math.random.nextInt(param) is annoying.
	
	//Characters
	static Knight knight = new Knight();			//The Knight/Archer/Wizard (based on what was picked in the Options screen). Referred to as "Hero"
	static Necromancer necro = new Necromancer();	//The Necromancer. Reminescent of "Blinky", the red ghost from Pac-man
	static Skeleton skele = new Skeleton();			//The Skeleton. Reminescent of "Pinky", the pink ghost from Pac-man
	static Wraith wraith = new Wraith();			//The Wraith. Reminescent of "Inky", the blue ghost from Pac-man
	static Wisp wisp = new Wisp();					//The Wisp. Reminescent of "Clyde", the orange ghost from Pac-man
	
	//Tile array
	static ArrayList<Wall> walls = new ArrayList<Wall>();					//The array of Walls
	static Wall[] teles = new Wall[2];										//The array of "teleporters" at the end of each tunnel on the map
	static ArrayList<Pellet> pellets = new ArrayList<Pellet>();				//The array of Coins (pellets) for the Hero to collect
	static ArrayList<Energizer> energizers = new ArrayList<Energizer>();	//The array of Energizers for the Hero
	static Wall ghostHome = new Wall(223, 104, 17, 1);						//The extra wall in front of the Ghosts' "home". Basically keeps them out of the box.
	
	//Object layers
	int objectLayer = 0;
	int pelletLayer = 1;
	
	//Timers that work with the game
	static Timer startTimer = new Timer();	//At the very beginning, lets the start theme finish before the game begins
	static Timer deathTimer = new Timer();	//When the knight dies, it temporarily pauses the game
	
	//Booleans that tell us when to start updating the newly spawned mobs
	static boolean skeleInited = false;		//For the Skeleton
	static boolean wraithInited = false;	//For the Wraith
	static boolean wispInited = false;		//For the Wisp
	Timer pinkyTimer = new Timer();			//The Skeleton (similar to Pinky from the original game) spawns after this timer goes out, typically 10 seconds 
	int maxPellets = 0;						//The number of maximum pellets, when this gets to certain numbers, the Wraith and the Wisp spawn, in that order
	
	@SuppressWarnings("unchecked")	//ID's couldn't be made, simply suppressing the warning. Doesn't hurt anything.
	@Override
	public void init(GameContainer container, StateBasedGame arg1) throws SlickException {	//The Init function that initializes most variables and other game components before gameplay begins and renders. Init functions, as well as Update() and Render(), are core features of almost every class file
		try{	//Initializing the TiledMap
			map = new TiledMap("specs/assets/fantasypac.tmx");
		} catch(SlickException e){
			e.printStackTrace();
		}
		
		try {		//initializing fonts
			awtfont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, org.newdawn.slick.util.ResourceLoader.getResourceAsStream("/Fonts/slkscr.ttf"));
			awtfont = awtfont.deriveFont(java.awt.Font.PLAIN, 12.f);
			
			slickfont = new UnicodeFont(awtfont);		//Font for what's not selected
			slickfont.addAsciiGlyphs();
			slickfont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			slickfont.addAsciiGlyphs();
			slickfont.loadGlyphs();
			
			selectedfont = new UnicodeFont(awtfont);	//Font for what is selected. Slightly more yellow then slickfont.
			selectedfont.addAsciiGlyphs();
			selectedfont.getEffects().add(new ColorEffect(new Color(255, 210, 150)));
			selectedfont.addAsciiGlyphs();
			selectedfont.loadGlyphs();
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		
		try{					//Initializing sounds
			firstTheme = new Sound("/specs/audio/fantasy_pacman_beginning.ogg");
			energized = new Sound("/specs/audio/fantasy_energizer.ogg");
			coin = new Sound[9];
			
			for(int i = 0; i<9; i++){
				coin[i] = new Sound("/specs/audio/coins/coin"+(i+1)+".ogg");
			}
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		timeout = new Timer(); //Setting Timeout
		timeout.set(-1.0f);	   //Starting the Timeout to immediately stop the clicking from the Menu
		
		knight.init();						//Running Knight's Init
		knight.state = Knight.State.RIGHT;	//Just prevents a glitch with the Knight's movement
		
		necro.init();	//Necromancer's Init. It's here as the Necromancer spawns in the game immediately
		
		//Initializing booleans for the other ghosts
		skeleInited = false;
		wraithInited = false;
		wispInited = false;
		pinkyTimer.set(0f);
		
		startTimer.set(0f);		//Starting the Start timer
		firstTheme.play();		//Playing the beginning theme
		
		//Making the walls
		for(int i = 0; i < map.getObjectCount(objectLayer); i++){
			walls.add(new Wall(map.getObjectX(objectLayer, i)+120, map.getObjectY(objectLayer, i)-19, map.getObjectWidth(objectLayer, i), map.getObjectHeight(objectLayer, i)));
		}
		walls.add(ghostHome);	//Necessary for keeping the ghosts out of the box
		
		//Initializing the teleporters at the tunnels
		teles[0] = new Wall(120, 110, 1, 20);
		teles[1] = new Wall(343, 110, 1, 20);
		
		//Making the coins/pellets
		if(Menu.restart){
			for(int i = 0; i < map.getObjectCount(pelletLayer); i++){
				pellets.add(new Pellet(map.getObjectX(pelletLayer, i)+120, map.getObjectY(pelletLayer, i)));
			}
		}
		
		//Making the energizers
		if(!Extras.getNoEner()){		//If "No Energizers" was turned on in the Extras screen, it turns the energizers into normal pellets. Otherwise it just simply makes the energizers.
			energizers.add(new Energizer(127, 44));
			energizers.add(new Energizer(327, 44));
			energizers.add(new Energizer(127, 197));
			energizers.add(new Energizer(327, 197));
		} else {
			pellets.add(new Pellet(127, 44));
			pellets.add(new Pellet(327, 44));
			pellets.add(new Pellet(127, 197));
			pellets.add(new Pellet(327, 197));
		}
		
		maxPellets = pellets.size();	//Marks the Max amount of Pellets on the map
		
		Menu.restart = false;
	}

	@Override
	public void render(GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException {	//Necessary for visualizing the game
		g.scale(3f, 3f);										  //Scales the entire screen by 3:1. Otherwise everything is incredibly tiny (and out of place)
		map.render(120, -20);									  //Renders the map
		g.setBackground(new org.newdawn.slick.Color(40, 40, 40)); //Makes the background the same color as the Map's dead space)
		
		for(int i = 0; i < pellets.size(); i++){	//draws the coins/pellets
			pellets.get(i).draw();
		}
		
		for(int i = 0; i < energizers.size(); i++){ //draws the energizers
			energizers.get(i).draw();
		}
		
		//draws the entrance to the ghost home
		g.drawRect(ghostHome.position.getX(), ghostHome.position.getY(), ghostHome.position.getWidth(), ghostHome.position.getHeight());
		
		//Depending on a few factors, it decides what to render on the screen and when, mainly revolving on the end game
		if(knight.state != Knight.State.DEATH && pellets.size() > 0){	//This is how the game normally runs about 99% of the time
			knight.render(g);		//Rendering the Knight
			
			slickfont.drawString(10, 10, "Score: "+knight.score);	//Drawing the score
			
			necro.render(g);		//Rendering the Necromancer
			if(skeleInited){
				skele.render(g);	//Rendering the Skeleton
			}
			if(wraithInited){
				wraith.render(g);	//Rendering the Wraith
			}
			if(wispInited){
				wisp.render(g);		//Rendering the Wisp
			}
		} else if(startTimer.getTime() > 0 && knight.state == Knight.State.DEATH){	//The death animation
			knight.render(g);
		} else {	//This is a very short interval that, a-la pac-man style, only renders the Hero immediately after he is touched by a ghost, or if he wins the stage
			if(Options.getChar() == 0){
				knight.knightDown.draw(knight.x, knight.y);	//for the Knight
			} else if(Options.getChar() == 1){
				knight.archerDown.draw(knight.x, knight.y);	//for the Archer
			} else if(Options.getChar() == 2){
				knight.wizardDown.draw(knight.x, knight.y);	//for the Wizard
			}
			
			//If the Hero beats the stage, the game will stop and display this string
			if(pellets.size() == 0 && energizers.size() == 0){
				slickfont.drawString(170, 40, "YOU BEAT THE STAGE!");
			}
			
			//The timer to go with this block
			deathTimer.set(-3f);
		}
		
		slickfont.drawString(380, 10, "Stage: "+Menu.getStage());	//Constantly draws the Stage number in the top-right of the screen
		
		//Below is a set of functions that displays how many lives are left for the Hero on the hud. When looking at the hud, the number of lives should display how many lives are left minus one counting the current life
		if(Menu.getLives() > 1){
			if(Options.getChar() == 0){
				knight.knightRight.draw(10, 230);
			} else if(Options.getChar() == 1){
				knight.archerRight.draw(10, 230);
			} else if(Options.getChar() == 2){
				knight.wizardRight.draw(10, 230);
			}
		}
		
		if(Menu.getLives() > 2){
			if(Options.getChar() == 0){
				knight.knightRight.draw(25, 230);
			} else if(Options.getChar() == 1){
				knight.archerRight.draw(25, 230);
			} else if(Options.getChar() == 2){
				knight.wizardRight.draw(25, 230);
			}
		}
		
		if(Menu.getLives() > 3){
			if(Options.getChar() == 0){
				knight.knightRight.draw(40, 230);
			} else if(Options.getChar() == 1){
				knight.archerRight.draw(40, 230);
			} else if(Options.getChar() == 2){
				knight.wizardRight.draw(40, 230);
			}
		}
		
		if(Menu.getLives() > 4){
			if(Options.getChar() == 0){
				knight.knightRight.draw(55, 230);
			} else if(Options.getChar() == 1){
				knight.archerRight.draw(55, 230);
			} else if(Options.getChar() == 2){
				knight.wizardRight.draw(55, 230);
			}
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame sbg, int arg2) throws SlickException {	//Updates the entire game. Where the main algorithms for everything happen.
		Timer.tick(); 		//Runs the Timers
		
		if(startTimer.getTime() > 5f && knight.state != Knight.State.DEATH && pellets.size() > 0){
			knight.update();	//Updates the Knight
			necro.update();		//Updates the Necromancer
			
			if(skeleInited){
				skele.update();	//Updates the Skeleton
			}
			if(wraithInited){
				wraith.update();//Updates the Wraith
			}
			if(wispInited){
				wisp.update();	//Updates the Wisp
			}
		}
		
		if(pellets.size() == 0 && energizers.size() == 0 && startTimer.getTime() > 0){	//Helps with timing once the Hero beats a stage
			startTimer.set(-2f);
		}
		
		if(startTimer.getTime() == 0 && pellets.size() == 0 && energizers.size() == 0){	//Once the victory timer (started in the block above) runs out, the game starts a new stage
			Menu.stageCount++;						//Adding to the stage count
			Menu.restart = true;					//Allows the pellets to regenerate
			sbg.getState(4).init(container, sbg);	//Re-initializing the instance, psuedo-creating a new stage
			sbg.enterState(4);						//Re-entering the "new" stage
		}
		
		if(knight.state == Knight.State.DEATH && deathTimer.getTime() == 0){	//The Death process 
			if(Menu.getLives() == 0){					//If there are no lives left, it's game over. Literally.
				sbg.addState(new GameOver());			//Makes the State
				sbg.getState(5).init(container, sbg);	//Initializing the Game Over Screen
				sbg.enterState(5);						//Goes into the Game Over Screen
			} else {									//If there are lives left, the game "restarts"
				sbg.getState(4).init(container, sbg);
				sbg.enterState(4);
			}
		}
		
		//Conditions for the different ghosts to spawn
		if(pinkyTimer.getTime() > 10 && !skeleInited){				//The Skeleton spawns after a short period into the game
			skele.init();		//Skeleton's init
			skeleInited = true;	//Sets the boolean true so this doesn't happen again
		}
		if(maxPellets > (pellets.size() + 30) && !wraithInited){	//The Wraith spawns after 30 pellets have been collected
			wraith.init();		//Wraith's init
			wraithInited = true;
		}
		if(pellets.size() < (maxPellets * .33) && !wispInited){		//The Wisp spawns when 1/3 of pellets remain on the map
			wisp.init();		//Wisp's init
			wispInited = true;
		}
		
	}

	@Override
	public int getID() {	//Returns the ID of this state, which is 4
		return 4;
	}

}
