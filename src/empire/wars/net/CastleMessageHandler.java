package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Castle;
import empire.wars.EmpireWars;
import empire.wars.EmpireWars.TEAM;
import empire.wars.NetworkEntity;

public class CastleMessageHandler extends EntityMessageHandler {
	public CastleMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}
	
	/**
	 * update the player's color.
	 * @param player. The player entity.
	 * @param msg. Message containing the color to set the player entity. 
	 */
	private void setColor(Castle castle, String msg) {
		TEAM team;
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
		} else {
			team = TEAM.RED;
		}
		castle.changeColor(team);
	}
	
	/**
	 * Makes network updates to the castle entity.
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Castle castle = (Castle)entity;
		
		if (categoryType.equals("SETCOLOR")) {
			this.setColor(castle, msg);
		}
	}


	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Castle castle = new Castle(0, 0, TEAM.RED);
		castle.setObjectUUID(objectUUID);
		castle.setObjectType("NETWORK");
		ew.getCastle().put(castle.getObjectUUID(), castle);
		return (NetworkEntity)castle;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getCastle();
	}
}
