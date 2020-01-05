package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Creep;
import empire.wars.EmpireWars;
import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;
import empire.wars.NetworkEntity;
import empire.wars.Player;

public class PlayerMessageHandler extends EntityMessageHandler {

	public PlayerMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}
	
	/**
	 * update the player's direction. This is used for direction updates.
	 * @param player. The player entity.
	 * @param msg. Message containing the direction to change to. 
	 */
	private void setDirection(Player player, String msg) {
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
		player.changeDirection(direction, this.ew);
	}
	
	/**
	 * update the player's color.
	 * @param player. The player entity.
	 * @param msg. Message containing the color to set the player entity. 
	 */
	private void setColor(Player player, String msg) {
		TEAM team;
		TEAM creepTeam;
		int[] tilePos = new int[2];
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
			creepTeam = TEAM.RED;
		} else {
			team = TEAM.RED;
			creepTeam = TEAM.BLUE;
		}
		player.changeColor(team);
		
		// create creeps for this player.
		if (this.ew.getSessionType().equals("SERVER")) {
			for (int i = 0; i < EmpireWars.PLAYER_CREEPS; i++) {
				tilePos = this.ew.randomizeEntityPos();
		        	Creep tempCreep = new Creep(
		        		this.ew.getTileWidth() * tilePos[0], this.ew.getTileHeight() * tilePos[1], 
		        		creepTeam, this.ew.map);
		        	tempCreep.setPlayerUUID(player.getObjectUUID());
		        	this.ew.getCreeps().put(tempCreep.getObjectUUID(), tempCreep);
		    }
		}
	}
	
	/**
	 * Makes network updates to the player entity. It also update the 
	 * position of the health bar to follow the players movement.
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Player player = (Player)entity;
		player.setHealthBarPos();
		
		if (categoryType.equals("SETDIRECTION")) {
			this.setDirection(player, msg);
		} else if (categoryType.equals("SETCOLOR")) {
			this.setColor(player, msg);
		} else if (categoryType.equals("SETHEALTH")) {
			double x = Double.parseDouble(msg);
			player.health.setCurrentHealth(x);
		}  else if (categoryType.equals("SETCOL")) {
			player.setExploded();
		} else if (categoryType.equals("SETJAIL")) {
			boolean jailed = msg.equals("1") ? true : false; 
			player.inJail = jailed;
		}
	}


	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Player player = new Player(ew.getTileWidth() * 4, ew.getTileHeight() * 4, 0, 0, TEAM.BLUE);
		player.setObjectUUID(objectUUID);
		player.setPort(this.msgPacket.getPort());
		player.setIpAddress(this.msgPacket.getIpAddress());
		player.setObjectType("NETWORK");
		ew.getClientPlayer().put(player.getObjectUUID(), player);
		return (NetworkEntity)player;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getClientPlayer();
	}
}
