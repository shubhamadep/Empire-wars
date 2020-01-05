package empire.wars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.net.Message;


public class ShieldFlags extends PowerUp {
	public final static int SHIELD_FLAG_DURATION = 10000;

	public ShieldFlags(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.SHOWCASE_SHIELD);
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
		for (Iterator<HashMap.Entry<UUID, Flag>> i = this.game.getFlags().entrySet().iterator(); i.hasNext(); ) {
			Flag flagTemp = i.next().getValue();
			if (flagTemp.team == player.team && this.game.player.getTeam() != player.getTeam()) {
					flagTemp.setShieldedTime(SHIELD_FLAG_DURATION);
			}
		}	
	}

	@Override
	public void handleServer(Player player) {
		this.handleClient(player);
	}

}
