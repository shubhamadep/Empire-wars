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

public class ClientLobbyState  extends BasicGameState {
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		Message msg = new Message(ew.getUsername(),"CONNECT");
		ew.getSocketClient().broadCastMessage(msg);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 300, 350);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 600, 350);
		g.setFont(ttf);
		g.drawString("Retry", 345, 400);
		g.drawString("Main Menu", 627, 400);
		g.drawString("Searching for a session to join...", 320, 180);
		g.setColor(Color.green);
		// if broadcast server is found
		if (ew.getBroadcastServer() != null) {
			ConnectedPlayers server = ew.getBroadcastServer();
			String username = server.getUsername();
			g.drawString(
				"Session hosted by " + username + " has been found.", 320, 205);
			g.drawString("Waiting for the host to start the game.", 320, 230);
		}
		
		g.resetFont();
		g.setColor(Color.white);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		EmpireWars ew = (EmpireWars)game;
		
		for (Iterator<Message> i = ew.getReceivedPackets().iterator(); i.hasNext(); ) {
			Message tempMessage = i.next();
			if (tempMessage.getMsgType().equals("CONNECT")) {
				Message.determineHandler(tempMessage, ew);
				i.remove();
			}
		}
		
		for (Iterator<Message> i = ew.getReceivedPackets().iterator(); i.hasNext(); ) {
			Message tempMessage = i.next();
			if (tempMessage.getMsgType().equals("START") && ew.getBroadcastServer() != null) {
				Message.determineHandler(tempMessage, ew);
				i.remove();
			}
		}
		
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// retry button
			// Sends broadcast packet to try find the server again
			if ((mouseX > 308 && mouseX < 424) && (mouseY > 381 && mouseY < 441)) {
				Message msg = new Message(ew.getUsername(),"CONNECT");
				ew.getSocketClient().broadCastMessage(msg);
			}
			// Main menu button
			if ((mouseX > 610 && mouseX < 724) && (mouseY > 381 && mouseY < 441)) {
				game.enterState(EmpireWars.MENU_STATE_ID);
			}
		}
	}

	@Override
	public int getID() {
		return EmpireWars.CLIENT_LOBBY_STATE_ID;
	}

}
