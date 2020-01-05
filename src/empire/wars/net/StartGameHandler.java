package empire.wars.net;

import empire.wars.EmpireWars;

/**
 * Processes the START message type. This is sent
 * by the server to signal the start of a game
 * 
 * @author peculiaryak
 *
 */
public class StartGameHandler {
	private Message msg;
	private EmpireWars game;
	
	public StartGameHandler(Message msg, EmpireWars game) {
		this.msg = msg;
		this.game = game;
		this.handle();
	}
	
	
	private void handle() {
		if (this.game.getCurrentStateID() != EmpireWars.PLAY_STATE_ID) {
			this.game.enterState(EmpireWars.PLAY_STATE_ID);
		}
	}
}
