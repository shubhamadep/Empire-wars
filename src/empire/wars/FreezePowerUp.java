package empire.wars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.net.Message;

public class FreezePowerUp extends PowerUp {
	public final static int FREEZE_DURATION = 20000;

	public FreezePowerUp(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.FREEZE_RSC);
	}
	
	@Override
	public void handleClient(Player player) {
		if (this.objectType.equals("ORIGINAL")) {
			String className = this.getClass().getSimpleName().toUpperCase();
			// only this player should process it
			String msg = player.team.toString();
			Message powerupUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "PROCESSPOWERUP", msg, className);
			this.game.sendPackets.add(powerupUpdate);
			this._handleServer(player);
		}
	};
	
	public void _handleServer(Player player) {
		if (this.game.player.getTeam() != player.getTeam()) {
			this.game.player.setFreezeTime(FREEZE_DURATION);
		}
	}

	@Override
	public void handleServer(Player player) {
		this.handleClient(player);
	}
}
