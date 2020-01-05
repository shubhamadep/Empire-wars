package empire.wars.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.Flag;
import empire.wars.NetworkEntity;
import empire.wars.ShieldFlags;
import empire.wars.EmpireWars.TEAM;

public class ShieldFlagsHandler extends EntityMessageHandler {
	public ShieldFlagsHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		ShieldFlags shieldFlagPowerup = new ShieldFlags(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		shieldFlagPowerup.setObjectUUID(objectUUID);
		shieldFlagPowerup.setObjectType("NETWORK");
		ew.getShieldPowerup().put(shieldFlagPowerup.getObjectUUID(), shieldFlagPowerup);
		return (NetworkEntity)shieldFlagPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getShieldPowerup();
	}
	
	/**
	 * Makes network updates to the shield flags power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		ShieldFlags shieldFlagPowerup = (ShieldFlags)entity;
		
		if (categoryType.equals("PROCESSPOWERUP")) {
			TEAM team;
			if (msg.equals("BLUE")) {
				team = TEAM.BLUE;
			} else {
				team = TEAM.RED;
			} 
			for (Iterator<HashMap.Entry<UUID, Flag>> i = this.ew.getFlags().entrySet().iterator(); i.hasNext(); ) {
				Flag flagTemp = i.next().getValue();
				if (flagTemp.getTeam() == team && ew.getPlayer().getTeam() != team) {
						flagTemp.setShieldedTime(ShieldFlags.SHIELD_FLAG_DURATION);
				}
			} 	
		}
		if (categoryType.equals("SETCOL")) {
			shieldFlagPowerup.setExploded();
		}
	}
}