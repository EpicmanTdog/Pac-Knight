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

public class Credits extends BasicGameState{
	//Fonts (see Adventure.java)
	java.awt.Font awtfont;
	UnicodeFont slickfont;
	UnicodeFont selectedfont;
	
	//Sound and timer
	Sound click;					//A simple click for when something is selected
	Timer timeout = Menu.timeout;	//The sound timeout (See Adventure.java)
	
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
		
		try{					//Initializing sounds
			click = new Sound("/specs/audio/click.ogg");
		} catch(SlickException e) {
			e.printStackTrace();
		}
		
		timeout.set(-1.0f); //start timer so it doesn't make wierd noises
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame arg1, Graphics g) throws SlickException {
		//Rendering text
		slickfont.drawString(500, 100, "Credits:");
		selectedfont.drawString(500, 150, "Development:");
		slickfont.drawString(500, 180, "Edisco LLC");
		selectedfont.drawString(500, 230, "Sound:");
		slickfont.drawString(500, 260, "Bandai Namco");
		slickfont.drawString(500, 290, "Soundbible.com");
		slickfont.drawString(500, 320, "Kevin Bouchard");
		slickfont.drawString(500, 350, "Luke.RUSTLTD");
		selectedfont.drawString(500, 400, "Graphics");
		slickfont.drawString(500, 430, "Bandai Namco");
		slickfont.drawString(500, 460, "Edisco LLC");
		
		selectedfont.drawString(700, 600, "Press A To Go Back");
	}

	@Override
	public void update(GameContainer container, StateBasedGame arg1, int arg2) throws SlickException {
		
		Input input = container.getInput();	//Allows controlled input
		
		Timer.tick();	//Ticks at the timer
		
		if(input.isButtonPressed(0, 1) && timeout.getTime() >= 0){	//Clicks when pressing "A", 0 being A, and 1 being the controller.
			if(!click.playing()){									//Make sure the sound doesn't destroy our ears and get distorted
				click.play();										//Plays the sound
			}
			
			timeout.set(-1.0f);			//Sets the sound timeout again
			
			Main.game.enterState(0);	//Once 'A' is pressed, the game will return to the Main Menu (Menu.java)
			
		}
	}

	@Override
	public int getID() {		//Returns the ID of the state, which is 3
		return 3;
	}

}
