package empire.wars.net;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.Flag;
import empire.wars.NetworkEntity;
import empire.wars.VanishingAct;
import empire.wars.EmpireWars.TEAM;

public class VanishingActHandler extends EntityMessageHandler {
	public VanishingActHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		VanishingAct vanishPowerup = new VanishingAct(
			ew.getTileWidth() * 4, ew.getTileHeight() * 4, this.ew);
		vanishPowerup.setObjectUUID(objectUUID);
		vanishPowerup.setObjectType("NETWORK");
		ew.getVanishPowerup().put(vanishPowerup.getObjectUUID(), vanishPowerup);
		return (NetworkEntity)vanishPowerup;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return this.ew.getVanishPowerup();
	}
	
	/**
	 * Makes network updates to the vanishing act power-up entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		VanishingAct vanishPowerUp = (VanishingAct)entity;
		
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
					flagTemp.setVanishTime(VanishingAct.VANISH_DURATION);
				}
			}
		} 	
		if (categoryType.equals("SETCOL")) {
			vanishPowerUp.setExploded();
		}
	}
}