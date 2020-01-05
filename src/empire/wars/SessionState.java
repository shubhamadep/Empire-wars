package empire.wars;

import java.awt.Font;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.ResourceManager;

public class SessionState extends BasicGameState {
	private TextField txtField;
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
		txtField = new TextField(container, ttf, 350, 230, 300, 30);
		txtField.setBackgroundColor(Color.white);
		txtField.setTextColor(Color.black);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		if (ew.getUsername() != null) {
			txtField.setText(ew.getUsername());
		}
		ew.killServers();
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {	
		txtField.render(container, g);
		g.drawImage(ResourceManager.getImage(EmpireWars.LOGO_IMG_RSC), 190, 10);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 240, 350);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 470, 350);
		g.drawImage(ResourceManager.getImage(EmpireWars.MENU_BUTTONS_RSC), 690, 350);
		g.setFont(ttf);
		g.drawString("Username:", 260, 240);
		g.drawString("Host", 290, 400);
		g.drawString("Join", 520, 400);
		g.drawString("Main Menu", 717, 400);
        g.resetFont();
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		EmpireWars ew = (EmpireWars)game;
		if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON)) {
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			// host button
			if ((mouseX > 244 && mouseX < 367) && (mouseY > 381 && mouseY < 441) && txtField.getText() != "") {
				ew.setUsername(txtField.getText());
				try {
					DatagramSocket serverSock = new DatagramSocket(
						EmpireWars.SERVER_PORT, InetAddress.getByName("0.0.0.0"));
					serverSock.setBroadcast(true);
					ew.startUpServers(serverSock);
					ew.setSessionType("SERVER");
					game.enterState(EmpireWars.SERVER_LOBBY_STATE_ID);
				} catch (SocketException | UnknownHostException e) {
					e.printStackTrace();
				}
			}
			//  join session
			if ((mouseX > 474 && mouseX < 597) && (mouseY > 381 && mouseY < 441) && txtField.getText() != "") {
				ew.setUsername(txtField.getText());
				try {
					DatagramSocket clientSock = new DatagramSocket();
					clientSock.setBroadcast(true);
					ew.startUpServers(clientSock);
					ew.setSessionType("CLIENT");
					game.enterState(EmpireWars.CLIENT_LOBBY_STATE_ID);
				} catch (SocketException e) {
					e.printStackTrace();
				}
			}
			
			if ((mouseX > 694 && mouseX < 819) && (mouseY > 381 && mouseY < 441)) {
				game.enterState(EmpireWars.MENU_STATE_ID);
			}
		}
		
		// change to play state for client
		
	}

	@Override
	public int getID() {
		return EmpireWars.SESSION_STATE_ID;
	}

}
