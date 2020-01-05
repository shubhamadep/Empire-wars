package empire.wars;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;

import jig.ResourceManager;


public class SplashScreenState extends BasicGameState {
	private int timer;
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		timer = 2000;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceManager.getImage(EmpireWars.SPLASH_SCREEN_IMG_RSC), 0, 0);
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 130, 450);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer -= delta;
		if (timer <= 0)
			game.enterState(EmpireWars.MENU_STATE_ID, new EmptyTransition(), new FadeInTransition());
	}

	@Override
	public int getID() {
		return EmpireWars.SPLASH_SCREEN_STATE_ID;
	}

}
