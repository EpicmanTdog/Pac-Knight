package com.edisco;

import java.awt.Color;
import java.awt.FontFormatException;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.Timer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class GameOver extends BasicGameState{
	//Fonts
	java.awt.Font awtfont;
	UnicodeFont slickfont;
	UnicodeFont selectedfont;
	
	//A Timer that waits ten seconds before changing the state back to the Main Menu (Menu.java)
	Timer timer = new Timer();
	
	public GameOver() {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer arg0, StateBasedGame arg1) throws SlickException {	//For context on Init(), Render(), and Update(), refer to Adventure.java
		try {		//initializing fonts
			awtfont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, org.newdawn.slick.util.ResourceLoader.getResourceAsStream("/Fonts/slkscr.ttf"));
			awtfont = awtfont.deriveFont(java.awt.Font.PLAIN, 50.f);
			
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
		
		timer.set(0f);
	}

	@Override
	public void render(GameContainer arg0, StateBasedGame arg1, Graphics arg2) throws SlickException {
		slickfont.drawString(500, 350, "GAME OVER");	//Simply, puts a large string that says "GAME OVER" in the center of the screen
	}

	@Override
	public void update(GameContainer container, StateBasedGame sbg, int arg2) throws SlickException {
		Timer.tick();				//Ticks at the timer
		
		if(timer.getTime() > 10){	//When the Timer reaches 10 seconds, turn back to the Main Menu (Menu.java)
			sbg.getState(0).init(container, sbg);
			sbg.enterState(0);		//Returns to Menu.java (The Main Menu)
		}
	}

	@Override
	public int getID() {		//Returns the State's ID, which is 5 in this case
		return 5;
	}

}
