package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.BananaPowerUp;
import empire.wars.NetworkEntity;

public class BananaPowerUpHandler extends EntityMessageHandler {
	
	
	public BananaPowerUpHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		BananaPowerUp bananaPowerup = new BananaPowerUp(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		bananaPowerup.setObjectUUID(objectUUID);
		bananaPowerup.setObjectType("NETWORK");
		ew.getBananaPowerup().put(bananaPowerup.getObjectUUID(), bananaPowerup);
		return (NetworkEntity)bananaPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getBananaPowerup();
	}
	
	/**
	 * Makes network updates to the banana power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		if (categoryType.equals("PROCESSPOWERUP")) {
			this.ew.getPlayer().health.setHealth(BananaPowerUp.incrementValue);;
		} 	
		
		if (categoryType.equals("SETCOL")) {
			BananaPowerUp bananaPowerUp = (BananaPowerUp)entity;
			bananaPowerUp.setExploded();
		}
	}
}
