package empire.wars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.net.Message;
import jig.Collision;
import jig.ResourceManager;

/*
 * Power-up base class. Defines how collision detection is done and provides abstract methods
 * that any other power-ups should implement. 
 * The server handles everything there is to do about a power-up
 */

public abstract class PowerUp extends NetworkEntity {
	protected EmpireWars game;
	private String imageRsc;
	
	public PowerUp(final float x, final float y, EmpireWars game, String imageRsc){
		super(x, y);
		this.game = game;
		this.imageRsc = imageRsc;
		addImageWithBoundingBox(ResourceManager.getImage(this.imageRsc));
	}
	
	/**
	 * Defines how the server should handle the power-up if the collision was by 
	 * a player connected to the server.
	 * 
	 * @param player. A client player that just collided with the power-up
	 */
	public void handleClient(Player player) {
		if (this.objectType.equals("ORIGINAL")) {
			String className = this.getClass().getSimpleName().toUpperCase();
			// only this player should process it
			String msg = "";
			Message powerupUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "PROCESSPOWERUP", msg, className);
			powerupUpdate.setSingleClient(true);
			powerupUpdate.setPort(player.getPort());
			powerupUpdate.setIpAddress(player.getIpAddress());
			this.game.sendPackets.add(powerupUpdate);
		}
	};
	/**
	 * Defines how the server should handle the power-up.
	 * 
	 * @param player. The server player
	 */
	public abstract void handleServer(Player player);
		
	private void checkCollision() {
		// check collision with client players
		for (Iterator<HashMap.Entry<UUID, Player>> i = this.game.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
			Player tempPlayer = i.next().getValue();
			Collision isPen = this.collides(tempPlayer);
			if (isPen != null && !this.isDestroyed()) {
				this.handleClient(tempPlayer);
				this.explode();
				return;
			}
		}
		// check collision with myself (server player)
		Collision isPen = this.collides(this.game.player);
		if (isPen != null && !this.isDestroyed()) {
			this.handleServer(this.game.player);
			this.explode();
		}
	}

	public void update() {
		if (this.game.getSessionType().equals("SERVER")) {
			this.networkUpdate(this.game);  // network updates
			this.checkCollision();
		}
	}
}