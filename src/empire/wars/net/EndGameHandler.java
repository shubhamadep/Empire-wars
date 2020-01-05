package empire.wars.net;

import empire.wars.EmpireWars;

/**
 * Processes the END message type. This is sent
 * by the server to signal the end of a game
 * 
 * @author peculiaryak
 *
 */
public class EndGameHandler {
	private Message msg;
	private EmpireWars game;
	
	public EndGameHandler(Message msg, EmpireWars game) {
		this.msg = msg;
		this.game = game;
		this.handle();
	}
	
	
	private void handle() {
		if (this.game.getCurrentStateID() != EmpireWars.GAMEOVERSTATE_ID) {
			this.game.enterState(EmpireWars.GAMEOVERSTATE_ID);
		}
	}
}
