package com.edisco;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;

import org.lwjgl.util.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class Extras extends BasicGameState{
	//Fonts
	java.awt.Font awtfont;
	UnicodeFont slickfont;
	UnicodeFont selectedfont;
	
	//Extras options
	String[] menuops = {"Ghosts Have Scatterbrain:", "No Energizers:", "Ludicrous Speed Mode:", "Back"};	//The different options used in this menu
	int selectedOption;		//The option that the cursor is hovering over
	
	//Sound and timer
	Sound click;					//A small click noise when something is clicked
	Timer timeout = Menu.timeout;	//The sound timeout to avoid destroyed ears
	
	//Where the options are stored
	static boolean scatterbrain = false;	//Makes the ghosts "Scatterbrained", where they just move in random directions rather than actually chase the Hero
	static boolean noEner = false;			//Replaces the 4 energizers on the map with normal pellets
	static boolean speedyMode = false;		//Makes everything REALLY fast
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {	//see Adventure.java for context on Init(), Render(), and Update()
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
		
		selectedOption = 0;		//starts the selected option
		
		try{					//Initializing sounds
			click = new Sound("/specs/audio/click.ogg");
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		timeout.set(-1.0f); //start timer so it doesn't make wierd noises
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics g) throws SlickException {
		
		//A for loop that creates the options for the menu
		int ix = 300, iy = 100;
		for(int i = 0; i<menuops.length; i++){
			if(selectedOption == i){
				selectedfont.drawString(ix, iy, menuops[i]);
			} else {
				slickfont.drawString(ix, iy, menuops[i]);
			}
		iy += 50;
		}
		
		//Draws the "On" or "Off" string based on the current state of the item in question
		if(scatterbrain){
			if(selectedOption == 0){
				selectedfont.drawString(900, 100, "On");
			} else {
				slickfont.drawString(900, 100, "On");
			}
		} else {
			if(selectedOption == 0){
				selectedfont.drawString(900, 100, "Off");
			} else {
				slickfont.drawString(900, 100, "Off");
			}
		}
		
		if(noEner){
			if(selectedOption == 1){
				selectedfont.drawString(630, 150, "On");
			} else {
				slickfont.drawString(630, 150, "On");
			}
		} else {
			if(selectedOption == 1){
				selectedfont.drawString(630, 150, "Off");
			} else {
				slickfont.drawString(630, 150, "Off");
			}
		}
		
		if(speedyMode){
			if(selectedOption == 2){
				selectedfont.drawString(790, 200, "On");
			} else {
				slickfont.drawString(790, 200, "On");
			}
		} else {
			if(selectedOption == 2){
				selectedfont.drawString(790, 200, "Off");
			} else {
				slickfont.drawString(790, 200, "Off");
			}
		}
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame arg1, int arg2) throws SlickException {
		
		Input input = container.getInput();	//Allows controller input to happen
		
		Timer.tick();	//Keeps the timer going
		
		if(input.isButtonPressed(0, 1) && timeout.getTime() >= 0){	//Clicks when pressing "A", 0 being A, and 1 being the controller.
			if(!click.playing()){									//Make sure the sound doesn't destroy our ears and get distorted
				click.play();										//Plays the click
			}
		
			//When the option is clicked, it will revert the boolean to it's opposite. (True becomes false, false becomes true)
			if(selectedOption == 0){
				scatterbrain = !scatterbrain;
			} else if(selectedOption == 1){
				noEner = !noEner;
			} else if(selectedOption == 2){
				speedyMode = !speedyMode;
			} else if(selectedOption == 3){
				Main.game.enterState(0);	//Returns to the Main Menu (Menu.java)
			}
			
			timeout.set(-0.2f);	//Sets the sound timeout
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
					selectedOption = 3;
				}
				timeout.set(-0.2f);	
			}
		}
		
	}
	
	//Bunch of getters for the different options
	@Override
	public int getID() {	//Not an option, but returns the current state's ID
		return 2;
	}

	public static boolean getScatterbrain() {	//Scatterbrain
		return scatterbrain;
	}

	public static boolean getNoEner() {			//No Energizers
		return noEner;
	}

	public static boolean getSpeedyMode() {		//Ludicrous Speed mode
		return speedyMode;
	}

}
