package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;
import empire.wars.StarPowerUp;

public class StarPowerUpHandler extends EntityMessageHandler {
	
	
	public StarPowerUpHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		StarPowerUp starPowerup = new StarPowerUp(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		starPowerup.setObjectUUID(objectUUID);
		starPowerup.setObjectType("NETWORK");
		ew.getStarPowerup().put(starPowerup.getObjectUUID(), starPowerup);
		return (NetworkEntity)starPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getStarPowerup();
	}
	
	/**
	 * Makes network updates to the key power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		if (categoryType.equals("PROCESSPOWERUP")) {
			this.ew.setStarBullets(this.ew.getStarBullets() + 1);
		} 	
		
		if (categoryType.equals("SETCOL")) {
			StarPowerUp starPowerup = (StarPowerUp)entity;
			starPowerup.setExploded();
		}
	}
}
