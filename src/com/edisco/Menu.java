package com.edisco;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;

import org.lwjgl.util.Timer;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Menu extends BasicGameState{
	//Declaring fonts
	java.awt.Font awtfont;
	UnicodeFont slickfont;
	UnicodeFont selectedfont;
	
	//Declaring menu options
	String[] menuops = {"Begin Adventure", "Options", "Extras", "Credits", "Exit"};
	int selectedOption;
	
	//Declaring sounds
	Sound click;
	Sound maintheme;
	static Timer timeout = new Timer();
	
	//Declaring logo and sprites
	Image logo;
	
	static SpriteSheet chars;	//The Spritesheet
	Animation kta;				//knight
	Animation nta;				//necromancer
	Animation sta;				//skeleton
	Animation gta;				//wraith
	Animation wta;				//wisp
	
	//Integers that relate to the actual game, but need to be stored here so they aren't reset
	static int lives;
	static int stageCount;
	static boolean restart;

	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {
		try {		//initializing fonts
			awtfont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, org.newdawn.slick.util.ResourceLoader.getResourceAsStream("/Fonts/slkscr.ttf"));
			awtfont = awtfont.deriveFont(java.awt.Font.PLAIN, 36.f);
			
			slickfont = new UnicodeFont(awtfont);
			slickfont.addAsciiGlyphs();
			slickfont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
			slickfont.addAsciiGlyphs();
			slickfont.loadGlyphs();
			
			selectedfont = new UnicodeFont(awtfont);
			selectedfont.addAsciiGlyphs();
			selectedfont.getEffects().add(new ColorEffect(new Color(255, 210, 150)));
			selectedfont.addAsciiGlyphs();
			selectedfont.loadGlyphs();
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		
		selectedOption = 0;	
		
		try{					//Initializing sounds
			click = new Sound("/specs/audio/click.ogg");
			maintheme = new Sound("/specs/audio/fantasy_pacman_theme.ogg");
			if(!maintheme.playing()){
				maintheme.loop();
			}
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		timeout.set(-1.0f);
		
		try{				//Initializing Logo
			logo = new Image("specs/assets/packnightlogo.png");
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		//Initializing sprites
		chars = new SpriteSheet("specs/assets/fantasyspritesheet.png", 16, 16);
		Image[] ktaf = {chars.getSprite(0, 0), chars.getSprite(1, 0)};
		kta = new Animation(ktaf, 300);
		kta.setPingPong(true);
		
		Image[] ntaf = {chars.getSprite(0, 12)};
		nta = new Animation(ntaf, 300);
		nta.setPingPong(true);
		
		Image[] staf = {chars.getSprite(1, 12), chars.getSprite(2, 12)};
		sta = new Animation(staf, 300);
		sta.setPingPong(true);
		
		Image[] gtaf = {chars.getSprite(3, 12), chars.getSprite(4, 12)};
		gta = new Animation(gtaf, 300);
		gta.setPingPong(true);
		
		Image[] wtaf = {chars.getSprite(5, 12), chars.getSprite(6, 12)};
		wta = new Animation(wtaf, 300);
		wta.setPingPong(true);
		
		//The in-game integers are made here to preserve them
		stageCount = 1;
		lives = 3;
		restart = true;
	}

	@Override
	public void render(GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException {
		//Rendering options
		int ix = 500, iy = 350;
		for(int i = 0; i<5; i++){
			if(selectedOption == i){
				selectedfont.drawString(ix, iy, menuops[i]);
			} else {
				slickfont.drawString(ix, iy, menuops[i]);
			}
			iy += 50;
		}
		
		//Rendering logo
		logo.draw(350, 100);
		
		//Rendering sprites
		kta.draw(920, 230, 112, 112);
		nta.draw(650, 230, 112, 112);
		sta.draw(550, 230, 112, 112);
		gta.draw(450, 230, 112, 112);
		wta.draw(350, 230, 112, 112);
		
		g.setBackground(new org.newdawn.slick.Color(0, 0, 0));
	}

	@Override
	public void update(GameContainer container, StateBasedGame sbg, int arg2) throws SlickException {
		//Creating Input
		Input input = container.getInput();
		
		//Timeout for switching between options. Deleting this will make switching impossibly hard.
		Timer.tick();
		
		if(input.isButtonPressed(0, 1)){	//Clicks when pressing "A", 0 being A, and 1 being the controller.
			if(!click.playing()){			//Make sure the sound doesn't destroy our ears and get distorted
				click.play();
			}
			//Does things based on currently selected option
			if(timeout.getTime() >= 0){
				if(selectedOption == 0){			//The "Adventure" (Adventure.java)
					maintheme.stop();
					sbg.addState(new Adventure());
					sbg.getState(4).init(container, sbg);
					sbg.enterState(4);
				} else if(selectedOption == 1){		//The Options Screen (Options.java)
					Main.game.enterState(1);
				} else if(selectedOption == 2){		//The Extras Screen (Extras.java)
					Main.game.enterState(2);
				} else if(selectedOption == 3){		//The Credits Screen (Credits.java)
					Main.game.enterState(3);
				} else if(selectedOption == 4){		//Exits the entire program
					System.exit(0);
				}
				timeout.set(-0.2f);			//Sets the sound timeout
			}
		}
		if(input.isControllerDown(1)){		//Checks if the downward d-pad button is pressed
			if(timeout.getTime() >= 0){		//allows movement if the timeout is done
				selectedOption += 1;		//moves the option
				if(selectedOption >= 5){	//if the option goes past "Exit", it loops back to "Begin"
					selectedOption = 0;
				}
				timeout.set(-0.2f);			//Resetting the timeout
			}
		}
		if(input.isControllerUp(1)){		//All this does is the same exact as above, except for the upward d-pad button
			if(timeout.getTime() >= 0){		//There is currently a bug that makes the controller automatically switch options
				selectedOption -= 1;		//To fix, just press the up button.
				if(selectedOption <= -1){
					selectedOption = 4;
				}
				timeout.set(-0.2f);	
			}
		}
		
		if(restart != true){
			restart = true;
		}
	}
	
	@Override
	public int getID() {			//ID of the state
		return 0;
	}
	
	public static int getStage(){	//Gets the current Stage that the hero is on
		return stageCount;
	}
	
	public static int getLives(){	//Gets the amount of lives the hero has left
		return lives;
	}
}
