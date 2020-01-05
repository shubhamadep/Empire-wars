package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.EmpireWars;
import empire.wars.Flag;
import empire.wars.NetworkEntity;
import empire.wars.EmpireWars.TEAM;

public class FlagMessageHandler extends EntityMessageHandler {

	public FlagMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Flag flagTemp;
		flagTemp = new Flag(ew.getTileWidth() * 4, ew.getTileHeight() * 4);
		flagTemp.setObjectUUID(objectUUID);
		flagTemp.setObjectType("NETWORK");
		ew.getFlags().put(flagTemp.getObjectUUID(), flagTemp);
		return (NetworkEntity)flagTemp;
	}
	
	/**
	 * update the flag's color.
	 * @param flag. The flag entity.
	 * @param msg. Message containing the color to set the flag entity. 
	 */
	private void setColor(Flag flag, String msg) {
		TEAM team;
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
		} else {
			team = TEAM.RED;
		}
		flag.changeTeam(team);
	}
	

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getFlags();
	}
	
	/**
	 * Makes network updates to the flag entity.
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Flag flag = (Flag)entity;
		
		if (categoryType.equals("SETCOLOR")) {
			this.setColor(flag, msg);
		}
		
	}
}
