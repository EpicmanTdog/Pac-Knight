package com.edisco;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class Main extends StateBasedGame{
	
	public Main(String name) {
		super(name);
	}
	
	public static Main game;
	public static AppGameContainer app;
	
	public static void main(String[] args) throws SlickException {
			game = new Main("Pac-Knight");		//Makes the game
			app = new AppGameContainer(game);	//creating container
			app.setDisplayMode(1360, 768, true);//making container fit to a 1360x768 res (so everything fits and looks nice)
			app.setShowFPS(false);				//Removes the annoying fps string
			app.setVSync(false);				//Puts in VSync
			app.setTargetFrameRate(60);			//Syncs game to 60fps, otherwise the game moves at hyperspeed
			app.start();						//Starts the game
	}

	@Override
	public boolean closeRequested() {		//makes sure the game can actually close
		return true;
	}

	@Override
	public String getTitle() {				//Returns the title of the game
		return "Pac-Knight";
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {	//List of game states
		addState(new Menu());
		addState(new Options());
		addState(new Extras());
		addState(new Credits());
	}
	

}
