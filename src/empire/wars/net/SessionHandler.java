package empire.wars.net;

import java.util.Arrays;

import empire.wars.EmpireWars;
import empire.wars.EmpireWars.TEAM;

/**
 * Handle connection message.
 * The client broadcasts CONNECTION messages.
 * Once they are received by the server the server 
 * add the client to the list of connectedPlayers and 
 * sends back a message to the client so the client can know 
 * where the server is.
 * Here "SERVER" refers to the central authority that 
 * send packets to everyone else.
 * 
 * @author peculiaryak
 *
 */
public class SessionHandler {
	private Message msg;
	private EmpireWars game;
	
	public SessionHandler(Message msg, EmpireWars game) {
		this.msg = msg;
		this.game = game;
		this.handle();
	}
	
	
	private void handle() {
		
		// if you are a client and you don't yet have a server
		if (game.getSessionType().equals("CLIENT") && game.getBroadcastServer() == null) {
			// set broadcast server
			TEAM tempTeam;
			String[] msgTemp = msg.getMsg().split("\\:");
			String color = msgTemp[msgTemp.length - 1];
			if (color.equals("BLUE")) {
				tempTeam = TEAM.BLUE;
			} else {
				tempTeam = TEAM.RED;
			}
			
			msgTemp = Arrays.copyOf(msgTemp, msgTemp.length - 1);
			String username = String.join(":", msgTemp);
			ConnectedPlayers player = new ConnectedPlayers(
				username, msg.getIpAddress(), msg.getPort(), TEAM.BLUE
			);
			game.setMyTeam(tempTeam);
			game.setBroadcastServer(player);
		} else if (game.getSessionType().equals("SERVER")) {
			// append the client to connected clients
			TEAM tempTeam;
			if (game.getConnectedPlayers().size() % 2 == 0) {
				tempTeam = TEAM.BLUE;
			} else {
				tempTeam = TEAM.RED;
			}
			ConnectedPlayers player = new ConnectedPlayers(
				msg.getMsg(), msg.getIpAddress(), msg.getPort(), tempTeam
			);
			game.appendConnectedPlayers(player);
			// construct message for client
			String msgTemp = game.getUsername() + ":" + tempTeam.toString(); 
			Message connectRes = new Message(msgTemp, "CONNECT");
			game.appendSendPackets(connectRes);
		}
	}
}
