package empire.wars.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.FreezePowerUp;
import empire.wars.NetworkEntity;
import empire.wars.Player;
import empire.wars.EmpireWars.TEAM;

public class FreezePowerUpHandler extends EntityMessageHandler {
	public FreezePowerUpHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		FreezePowerUp freezePowerup = new FreezePowerUp(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		freezePowerup.setObjectUUID(objectUUID);
		freezePowerup.setObjectType("NETWORK");
		ew.getFreezePowerup().put(freezePowerup.getObjectUUID(), freezePowerup);
		return (NetworkEntity)freezePowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getFreezePowerup();
	}
	
	/**
	 * Makes network updates to the vanishing act power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		FreezePowerUp freezePowerup = (FreezePowerUp)entity;
		
		if (categoryType.equals("PROCESSPOWERUP")) {
			TEAM team;
			if (msg.equals("BLUE")) {
				team = TEAM.BLUE;
			} else {
				team = TEAM.RED;
			}
			if (ew.getPlayer().getTeam() != team) {
				ew.getPlayer().setFreezeTime(FreezePowerUp.FREEZE_DURATION);
			}
		} 	
		
		if (categoryType.equals("SETCOL")) {
			freezePowerup.setExploded();
		}
	}
}
