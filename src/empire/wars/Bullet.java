package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.Vector;
import jig.ResourceManager;

public class Bullet extends NetworkEntity {
	private Vector velocity;
	private String bullet_image;
	private String _bullet_image = EmpireWars.PLAYER_BULLETIMG_RSC;
	private int bullet = 0;
	private int _bullet = 0;
	
	
	public enum BULLET_TYPE
	{
		PLAYER,
		CREPES,
		CASTLE,
		
	}
	
	private BULLET_TYPE bullet_type;
	private int start_x, start_y;
	
	public Bullet(final float x, final float y, final float vx, final float vy, final String in_bullet_image, final BULLET_TYPE in_bullet_type, final TEAM in_team, final int bullet){
		super(x,y);
		this.start_x = (int)x/32;
		this.start_y = (int)y/32;
		this.velocity = new Vector(vx, vy);
		this.bullet_image = in_bullet_image;
		this.bullet_type = in_bullet_type;
		this.team = in_team;
		this.bullet = bullet;
		addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}

	/**
	 * The server decides on creep collision detection. This is here so the 
	 * server can alert the client that created this bullet that they should delete it.
	 * @param game
	 */
	public void serverSendCollisionUpdates(EmpireWars game) {
		String className = this.getClass().getSimpleName().toUpperCase();
		Message posUpdate = new Message(
			this.getObjectUUID(), "UPDATE", "SETSERVERCOL", "", className);
		game.sendPackets.add(posUpdate);
	}
	
	/*
	 * collision between the bullets and the walls
	 */
	private void checkCollision(EmpireWars ew) {
		if (!this.objectType.equals("ORIGINAL")) {
			return;
		}
		
		if (this.bullet_type == BULLET_TYPE.CASTLE)
		{
			int x_distance = Math.abs(this.start_x - (int)this.getX()/32);
			int y_distance = Math.abs(this.start_y - (int)this.getY()/32);
			if (x_distance + y_distance > 25)
			{
				this.explode();
			}
		}
		else
		{
			int bx = (int) this.getX() / 32;
			int by = (int) this.getY() / 32;
			int wallIndex = ew.map.getLayerIndex("walls");
			if (ew.map.getTileId(bx, by, wallIndex) != 0 ) {
				this.explode();
			}
		}
	}
	
	/*
	 * Used by the network to change the bullet image.
	 */
	public void setCastleFireImage() {
		removeImage(ResourceManager.getImage(EmpireWars.PLAYER_BULLETIMG_RSC));
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FIRE_IMAGE_RSC));
	}
	
	/**
	 * Used by the network to set the players color to the right color.
	 * @param team
	 */
	public void changeColor(TEAM team) {
		this.team = team;
	}
	
	/*
	 * Update clients on my strength color I belong to
	 */
	public void sendTypeUpdate(EmpireWars game) {
		if (this.objectType.equals("ORIGINAL") && this._bullet != this.bullet) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = Integer.toString(this.bullet);
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETTYPE", msg, className);
			game.sendPackets.add(posUpdate);
			this._bullet = this.bullet; 
		}
	}
	
	private void sendImageUpdate(EmpireWars game) {
		if (this.objectType == "ORIGINAL" && this._bullet_image != this.bullet_image) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = "";
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETCASTLE", msg, className);
			game.sendPackets.add(posUpdate);
			this._bullet_image = this.bullet_image; 
		}
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
		this.sendTypeUpdate(game);
		this.sendImageUpdate(game);
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		EmpireWars ew = (EmpireWars) game;
		this.checkCollision(ew);
		translate(velocity.scale(delta));
		this.networkUpdate(ew);  // network updates
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}

	public int getBullet() {
		return bullet;
	}

	public void setBullet(int bullet) {
		this.bullet = bullet;
	}
}
