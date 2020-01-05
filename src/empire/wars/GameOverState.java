package empire.wars;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.HorizontalSplitTransition;

import jig.ResourceManager;


public class GameOverState extends BasicGameState {
	private int timer;
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 20);
		ttf = new TrueTypeFont(font, false);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		EmpireWars ew = (EmpireWars)game;
		timer = 4000;
		ew.killServers();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(ResourceManager.getImage(EmpireWars.GAMEOVER_IMG_RSC), 360, 200);
		g.setFont(ttf);
		g.setColor(Color.blue);
		g.drawString("Blue Team: " + ew.getScore().getBlueTeam(), 360, 350);
		g.setColor(Color.red);
		g.drawString("Red Team: " + ew.getScore().getRedTeam(), 360, 300);
		g.setColor(Color.white);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		timer -= delta;
		if (timer <= 0)
			game.enterState(EmpireWars.MENU_STATE_ID, new EmptyTransition(), new HorizontalSplitTransition() );
	}

	@Override
	public int getID() {
		return EmpireWars.GAMEOVERSTATE_ID;
	}

}
