package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.KeyPowerUp;
import empire.wars.NetworkEntity;

public class KeyPowerUpHandler extends EntityMessageHandler {
	
	
	public KeyPowerUpHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		KeyPowerUp keyPowerup = new KeyPowerUp(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		keyPowerup.setObjectUUID(objectUUID);
		keyPowerup.setObjectType("NETWORK");
		ew.getKeyPowerup().put(keyPowerup.getObjectUUID(), keyPowerup);
		return (NetworkEntity)keyPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getKeyPowerup();
	}
	
	/**
	 * Makes network updates to the key power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		if (categoryType.equals("PROCESSPOWERUP")) {
			this.ew.setFreeCards(this.ew.getFreeCards() + 1);
		} 	
		
		if (categoryType.equals("SETCOL")) {
			KeyPowerUp keyPowerup = (KeyPowerUp)entity;
			keyPowerup.setExploded();
		}
	}
}
