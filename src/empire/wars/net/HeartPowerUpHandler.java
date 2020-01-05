package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.HeartPowerUp;
import empire.wars.NetworkEntity;

public class HeartPowerUpHandler extends EntityMessageHandler {
	
	
	public HeartPowerUpHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		HeartPowerUp heartPowerup = new HeartPowerUp(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		heartPowerup.setObjectUUID(objectUUID);
		heartPowerup.setObjectType("NETWORK");
		ew.getHeartPowerup().put(heartPowerup.getObjectUUID(), heartPowerup);
		return (NetworkEntity)heartPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getHeartPowerup();
	}
	
	/**
	 * Makes network updates to the heart power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		if (categoryType.equals("PROCESSPOWERUP")) {
			this.ew.setLives(this.ew.getLives() + 1);
		} 	
		
		if (categoryType.equals("SETCOL")) {
			HeartPowerUp heartPowerUp = (HeartPowerUp)entity;
			heartPowerUp.setExploded();
		}
	}
}
