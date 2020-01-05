package empire.wars;

import java.awt.Font;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.net.ConnectedPlayers;
import empire.wars.net.Message;
import jig.ResourceManager;

public class ServerLobbyState extends BasicGameState {
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		int counter = 1;
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 300, 350);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 600, 350);
		g.setFont(ttf);
		g.drawString("Start", 350, 400);
		g.drawString("Main Menu", 627, 400);
		g.resetFont();
		g.drawString("Waiting for clients to join the session...", 320, 180);
		g.setColor(Color.green);
		for (Iterator<ConnectedPlayers> i = ew.getConnectedPlayers().iterator(); i.hasNext(); ) {
			ConnectedPlayers tempPlayer = i.next();
			g.drawString(counter + ". " + tempPlayer.getUsername(), 320, 180 + (25 * counter));
			counter++;
		}
		g.resetFont();
		g.setColor(Color.white);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		EmpireWars ew = (EmpireWars)game;

		Input input = container.getInput();
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// start game button
			if ((mouseX > 308 && mouseX < 424) && (mouseY > 381 && mouseY < 441)) {
				Message msg = new Message("", "START"); 
				ew.appendSendPackets(msg);
				game.enterState(EmpireWars.PLAY_STATE_ID);
			}
			//  return to menu
			if ((mouseX > 610 && mouseX < 724) && (mouseY > 381 && mouseY < 441)) {
				game.enterState(EmpireWars.MENU_STATE_ID);
			}
		}
		
		for (Iterator<Message> i = ew.getReceivedPackets().iterator(); i.hasNext(); ) {
			Message tempMessage = i.next();
			if (tempMessage.getMsgType().equals("CONNECT")) {
				Message.determineHandler(tempMessage, ew);
				i.remove();
			}
		}
	}

	@Override
	public int getID() {
		return EmpireWars.SERVER_LOBBY_STATE_ID;
	}

}
