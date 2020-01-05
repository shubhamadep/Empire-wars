package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Creep;
import empire.wars.EmpireWars;
import empire.wars.NetworkEntity;
import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;

public class CreepMessageHandler extends EntityMessageHandler {

	public CreepMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	/**
	 * Makes network updates to the creep entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Creep creep = (Creep)entity;
		creep.setHealthBarPos();
		
		if (categoryType.equals("SETDIRECTION")) {
			Direction direction = Direction.UP;
			if (msg.equals("UP")) {
				direction = Direction.UP;
			} else if (msg.equals("DOWN")) {
				direction = Direction.DOWN;
			} else if (msg.equals("LEFT")) {
				direction = Direction.LEFT;
			} else if (msg.equals("RIGHT")) {
				direction = Direction.RIGHT;
			}
			creep.changeDirection(direction, this.ew);
		} else if (categoryType.equals("SETCOLOR")) {
			this.setColor(creep, msg);
		} else if (categoryType.equals("SETCOL")) {
			creep.setExploded();
		} else if (categoryType.equals("SETPLAYERLIFE")) {
			double x = Double.parseDouble(msg);
			ew.getPlayer().health.setHealth(x);
		} else if (categoryType.equals("SETHEALTH")) {
			double x = Double.parseDouble(msg);
			creep.health.setCurrentHealth(x);
		}
	}
	
	/**
	 * update the player's color.
	 * @param player. The player entity.
	 * @param msg. Message containing the color to set the player entity. 
	 */
	private void setColor(Creep creep, String msg) {
		TEAM team;
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
		} else {
			team = TEAM.RED;
		}
		creep.changeColor(team);
	}
	
	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Creep creepTemp;
		creepTemp = new Creep(ew.getTileWidth() * 4, ew.getTileHeight() * 4, TEAM.BLUE, ew.map);
		creepTemp.setObjectUUID(objectUUID);
		creepTemp.setObjectType("NETWORK");
		ew.getCreeps().put(creepTemp.getObjectUUID(),creepTemp);
		return (NetworkEntity)creepTemp;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getCreeps();
	}
}
