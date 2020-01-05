package empire.wars.net;

import java.util.HashMap;
import java.util.UUID;

import empire.wars.Bullet;
import empire.wars.EmpireWars;
import empire.wars.EmpireWars.TEAM;
import empire.wars.NetworkEntity;
import empire.wars.Bullet.BULLET_TYPE;

public class BulletMessageHandler extends EntityMessageHandler {

	public BulletMessageHandler(Message msg, EmpireWars ew) {
		super(msg, ew);
	}

	/**
	 * Makes network updates to the bullet entity. 
	 */
	@Override
	public void update(NetworkEntity entity, String categoryType, String msg) {
		super.update(entity, categoryType, msg);
		Bullet bullet = (Bullet)entity;
		
		if (categoryType.equals("SETCOL")) {
			bullet.setExploded();
		} else if (categoryType.equals("SETCOLOR")) {
			this.setColor(bullet, msg);
		} else if (categoryType.equals("SETTYPE")) {
			int strength = Integer.parseInt(msgPacket.getMsg());
			bullet.setBullet(strength);
		} else if (categoryType.equals("SETSERVERCOL") && bullet.getObjectType().equals("ORIGINAL")) {
			bullet.explode();
		} else if (categoryType.equals("SETCASTLE")) {
			bullet.setCastleFireImage();
		}
	}
	
	/**
	 * update the player's color.
	 * @param bullet. The player entity.
	 * @param msg. Message containing the color to set the player entity. 
	 */
	private void setColor(Bullet bullet, String msg) {
		TEAM team;
		if (msg.equals("BLUE")) {
			team = TEAM.BLUE;
		} else {
			team = TEAM.RED;
		}
		bullet.changeColor(team);
	}
	
	@Override
	public NetworkEntity createEntity(UUID objectUUID) {
		Bullet bulletTemp;
		bulletTemp = new Bullet(
			0f, 0f, 0f, 0f, EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER, TEAM.BLUE, 0);
		bulletTemp.setObjectUUID(objectUUID);
		bulletTemp.setObjectType("NETWORK");
		ew.getClientBullets().put(bulletTemp.getObjectUUID(), bulletTemp);
		return (NetworkEntity)bulletTemp;
	}

	@Override
	public HashMap<UUID, ? extends NetworkEntity> getHashMap() {
		return ew.getClientBullets();
	}
}
