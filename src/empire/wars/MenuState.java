package empire.wars;


import java.awt.Font;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.ResourceManager;

/**
 * Implements the menu screen. The menu screen will have the option to 
 * play the game and exit the game
 * 
 * Menu buttons resource courtesy of verique
 * https://opengameart.org/content/fantasy-buttons-0
 * 
 * @author peculiaryak
 *
 */


public class MenuState extends BasicGameState {
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		AppGameContainer gc = (AppGameContainer) container;
		gc.setDisplayMode(EmpireWars.SCREEN_WIDTH ,EmpireWars.SCREEN_HEIGHT, false);		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 300, 350);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 600, 350);
		g.setFont(ttf);
		g.drawString("Play", 350, 400);
		g.drawString("Exit", 650, 400);
		g.resetFont();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// play button
			if ((mouseX > 308 && mouseX < 424) && (mouseY > 381 && mouseY < 441)) {
				game.enterState(EmpireWars.SESSION_STATE_ID);
			}
			//  click on exit button
			if ((mouseX > 610 && mouseX < 724) && (mouseY > 381 && mouseY < 441)) {
				System.exit(0);
			}
		}
		
	}

	@Override
	public int getID() {
		return EmpireWars.MENU_STATE_ID;
	}

}
