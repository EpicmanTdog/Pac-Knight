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

public class Options extends BasicGameState{
	
	//redeclaring fonts
	java.awt.Font awtfont;
	UnicodeFont slickfont;
	UnicodeFont selectedfont;
	
	//Declaring menu options
	String[] menuops = {"Character: ", "Reset High Score", "Back"};
	String[] charops = {"Knight", "Archer", "Wizard"};
	int selectedOption;
	static int selectedChar = 0;
	
	//Declaring sounds
	Sound click;
	Timer timeout = Menu.timeout;
	
	//High Score
	int highscore = 0;
	
	SpriteSheet chars;
	Animation koa; //Knight Options Anim
	Animation aoa; //Archer Options Anim
	Animation woa; //Wizard Options Anim
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {	//See Adventure.java for the importance of Init(), Update(), and Render()
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
		
		//Initializing options
		selectedOption = 0;
		selectedChar = 0;
		
		try{					//Initializing sounds
			click = new Sound("/specs/audio/click.ogg");
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		timeout.set(-1.0f);		//Sets the sound timeout
		
		//The sprites to show
		chars = new SpriteSheet("specs/assets/fantasyspritesheet.png", 16, 16);
		
		Image[] koaf = {chars.getSprite(0, 0), chars.getSprite(1, 0)};
		koa = new Animation(koaf, 300);
		koa.setPingPong(true);
		
		Image[] aoaf = {chars.getSprite(0, 4), chars.getSprite(1, 4)};
		aoa = new Animation(aoaf, 300);
		aoa.setPingPong(true);
		
		Image[] woaf = {chars.getSprite(0, 8), chars.getSprite(1, 8)};
		woa = new Animation(woaf, 300);
		woa.setPingPong(true);
		
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
		//Rendering the options
		int ix = 500, iy = 100;
		for(int i = 0; i<3; i++){
			if(selectedOption == i){
				selectedfont.drawString(ix, iy, menuops[i]);
			} else {
				slickfont.drawString(ix, iy, menuops[i]);
			}
		iy += 50;
		}
		if(selectedOption == 0){
			selectedfont.drawString(750, 100, charops[selectedChar]);
		} else {
			slickfont.drawString(750, 100, charops[selectedChar]);
		}
		
		//Draws the hero as he/she is portrayed in the game
		if(selectedChar == 0){
			koa.draw(900, 100, 112, 112);
		} else if(selectedChar == 1){
			aoa.draw(900, 100, 112, 112);
		} else if(selectedChar == 2){
			woa.draw(900, 110, 112, 112);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame arg1, int arg2) throws SlickException {
		//Creating Input
		Input input = container.getInput();
			
		//Timeout for switching between options. Deleting this will make switching impossibly hard.
		Timer.tick();
		
		if(input.isButtonPressed(0, 1) && timeout.getTime() >= 0){	//Clicks when pressing "A", 0 being A, and 1 being the controller.
			if(!click.playing()){									//Make sure the sound doesn't destroy our ears and get distorted
				click.play();
			}
			
			//Does things based on currently selected option
			if(selectedOption == 0){
				selectedChar += 1;
				if(selectedChar >= 3){
					selectedChar = 0;
				}
			} else if(selectedOption == 1){
				highscore = 0;
			} else if(selectedOption == 2){
				Main.game.enterState(0);
			}
				timeout.set(-0.2f);
		}
		if(input.isControllerDown(1)){		//Checks if the downward d-pad button is pressed
			if(timeout.getTime() >= 0){		//allows movement if the timeout is done
				selectedOption += 1;		//moves the option
				if(selectedOption >= 3){	//if the option goes past "Exit", it loops back to "Begin"
					selectedOption = 0;
				}
				timeout.set(-0.2f);			//Resetting the timeout
			}
		}
		if(input.isControllerUp(1)){		//All this does is the same exact as above, except for the upward d-pad button
			if(timeout.getTime() >= 0){		//There is currently a bug that makes the controller automatically switch options
				selectedOption -= 1;		//To fix, just press the up button.
				if(selectedOption <= -1){
					selectedOption = 2;
				}
				timeout.set(-0.2f);	
			}
		}
	}

	@Override
	public int getID() {			//Returns the ID of the state, 1
		return 1;
	}
	
	public static int getChar(){	//Returns the currently selected character
		return selectedChar;
	}
	
}
