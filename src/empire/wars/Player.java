package empire.wars;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.Bullet.BULLET_TYPE;
import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.ResourceManager;
import jig.Vector;

public class Player extends NetworkEntity {
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head
	Random rand = new Random();
	public Direction direction;
	private Direction _direction;
	private InetAddress ipAddress;
	private int port;
	public Vector tilePosition;
	private Vector blueStart = new Vector(6085, 1490);
	private Vector redStart = new Vector(120, 118);
	private Vector redJail = new Vector(128, 1408);
	private Vector blueJail = new Vector(6240, 160);
	public boolean inJail = false;
	private boolean _inJail = false;
	private int JAILTIME = 15000;
	private int jailTime = JAILTIME;
	
	private int freeze_stay_timer = 0;
	
	private Animation blue_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 3, 2, 3, true, 150, true);
	private Animation blue_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 0, 2, 0, true, 150, true);
	private Animation blue_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 1, 2, 1, true, 150, true);
	private Animation blue_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.BLUE_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 2, 2, 2, true, 150, true);
	
	private Animation red_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 3, 2, 3, true, 150, true);
	private Animation red_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 0, 2, 0, true, 150, true);
	private Animation red_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 1, 2, 1, true, 150, true);
	private Animation red_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.RED_PLAYER_MOVING_IMG_RSC, 48, 48), 0, 2, 2, 2, true, 150, true);
	
	public Player(final float x, final float y, final float vx, final float vy, final TEAM in_team){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, in_team);
		this.team = in_team;
		this.tilePosition = getTileIdx(new Vector(x, y));

		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
		addAnimation(getAnimation(direction));
		this.setPlayerStartPosition();
	}
	
	private void setPlayerStartPosition() {
		if (this.team == TEAM.RED) {
			this.setPosition(redStart);
		} else if (this.team == TEAM.BLUE) {
			this.setPosition(blueStart);
		}
		this.setHealthBarPos();
	}
	
	private void sentToJail(){
		if(this.team == TEAM.BLUE){
			this.setPosition(blueJail);
		}else if(this.team == TEAM.RED){
			this.setPosition(redJail);
		}
		this.setHealthBarPos();
	}
	
	/**
	 * Kills the player and reduces a life.
	 * It also takes the player to limbo where they await judgement.
	 */
	private void killPlayer(EmpireWars ew) {
		if (this.health.getCurrentHealth() <= 0 && ew.getLives() <= 0) {
//			this.explode();
			this.sentToJail();
			this.inJail = true;
			this.health.setCurrentHealth(HealthBar.MAXIMUM_HEALTH);
			ew.setLives(EmpireWars.MAX_LIVES);
			
//			this.exploded = false;
		} else if (this.health.getCurrentHealth() <= 0) {
			this.health.setCurrentHealth(HealthBar.MAXIMUM_HEALTH);
			ew.setLives(ew.getLives() - 1);
			this.setPlayerStartPosition();
		}
	}
	
	
	public void shoot(EmpireWars game, int bulletNature){
		Bullet bullet;
		ResourceManager.getSound(EmpireWars.PLAYER_SHOOTSND_RSC).play();
		switch(direction){
		case UP:
			bullet = new Bullet(getX(), getY(), 0.f, -EmpireWars.PLAYER_BULLETSPEED, 
				EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER, this.team, bulletNature);
			game.clientBullets.put(bullet.getObjectUUID(), bullet);
			break;
		case DOWN:
			bullet = new Bullet(getX(), getY(), 0.f, EmpireWars.PLAYER_BULLETSPEED, 
				EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER, this.team, bulletNature);
			game.clientBullets.put(bullet.getObjectUUID(), bullet);
			break;
		case LEFT:
			bullet = new Bullet(getX(), getY(), -EmpireWars.PLAYER_BULLETSPEED, 0.f, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER, this.team, bulletNature);
			game.clientBullets.put(bullet.getObjectUUID(), bullet);
			break;
		case RIGHT:
			bullet =  new Bullet(getX(), getY(), EmpireWars.PLAYER_BULLETSPEED, 0.f, 
					EmpireWars.PLAYER_BULLETIMG_RSC, BULLET_TYPE.PLAYER, this.team, bulletNature);
			game.clientBullets.put(bullet.getObjectUUID(), bullet);
			break;
		default:
			break;
		}
	}
	
	public Animation getAnimation(Direction direction)
	{
		if (team == TEAM.BLUE)
		{
			switch (direction)
			{
			case UP:
				return blue_movement_up;
			case DOWN:
				return blue_movement_down;
			case LEFT:
				return blue_movement_left;
			case RIGHT:
				return blue_movement_right;
			default:
				return blue_movement_down;
			}
		}
		else
		{
			switch (direction)
			{
			case UP:
				return red_movement_up;
			case DOWN:
				return red_movement_down;
			case LEFT:
				return red_movement_left;
			case RIGHT:
				return red_movement_right;
			default:
				return red_movement_down;
			}
		}
	}
	
	/*
	 * ipAddress getter
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/*
	 * ipAddress setter
	 */
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	/*
	 * team getter
	 */
	public TEAM getTeam() {
		return this.team;
	}

	/**
	 * port getter
	 */
	public int getPort() {
		return port;
	}

	/*
	 * port setter
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setFreezeTime(int freeze_stay_timer) {
		this.freeze_stay_timer = freeze_stay_timer;
		System.out.println(this.freeze_stay_timer);
	}
	
	public void changeDirection(Direction new_direction, EmpireWars game)
	{
		removeAnimation(getAnimation(Direction.UP));
		removeAnimation(getAnimation(Direction.DOWN));
		removeAnimation(getAnimation(Direction.LEFT));
		removeAnimation(getAnimation(Direction.RIGHT));
		addAnimation(getAnimation(new_direction));
		direction = new_direction;
		// update clients on player position
		this.sendDirectionUpdates(game, "SETDIRECTION");
	}
	
	/**
	 * Update client on the direction i am facing.
	 * This allows for the animations behave correctly
	 * @param game. The current Game State.
	 * @param categoryType. The message category type.
	 * Allows the receiver to know what to do with it.
	 */
	private void sendDirectionUpdates(EmpireWars game, String categoryType) {
		if (this.objectType.equals("ORIGINAL") && this.direction != this._direction) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = this.direction.toString();
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", categoryType, msg, className);
			game.sendPackets.add(posUpdate);
			this._direction = this.direction;
		}
	}
	/**
	 * Sends health bar updates
	 * @param game. The current game state
	 */
	public void sendHealthBarUpdates(EmpireWars game) {
		if (this.objectType.equals("ORIGINAL") && this.health.hasChanged()) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = Double.toString(this.health.getCurrentHealth());
			Message healthUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETHEALTH", msg, className);
			game.sendPackets.add(healthUpdate);
		}
	}
	
	/**
	 * Sends in jail updates
	 * @param game. The current game state
	 */
	public void sendInjailUpdates(EmpireWars game) {
		if (this.objectType.equals("ORIGINAL") && this._inJail != this.inJail) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = this.inJail ? "1" : "0";
			Message jailUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETJAIL", msg, className);
			game.sendPackets.add(jailUpdate);
			this._inJail = this.inJail;
		}
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
		this.sendHealthBarUpdates(game);
		this.sendInjailUpdates(game);
	}
	
	/**
	 * Used by the network to set the players color to the right color.
	 * @param team
	 */
	public void changeColor(TEAM team) {
		removeAnimation(getAnimation(Direction.UP));
		removeAnimation(getAnimation(Direction.DOWN));
		removeAnimation(getAnimation(Direction.LEFT));
		removeAnimation(getAnimation(Direction.RIGHT));
		this.team = team;
		this.health.setTeam(team);
		this.setPlayerStartPosition();
		addAnimation(getAnimation(this.direction));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		// dead player
		// we cannot delete the entity since the camera class needs it.
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		if (this.isExploded()) {
			return;
		}
		// get user input		
		Input input = container.getInput();
	
		Vector previousPoition = this.getPosition();
		this.killPlayer(ew);
		
		if(inJail == true){
			if(input.isKeyDown(Input.KEY_F) && ew.getFreeCards() > 0){
				inJail = false;
				setPlayerStartPosition();
				jailTime = 0;
				ew.setFreeCards(ew.getFreeCards() - 1);
			} else if(jailTime > 0){
				setVelocity(new Vector(0.f, 0.f));
				jailTime -= delta;
				return;
			} else if (jailTime <= 0) {
				jailTime = JAILTIME;
				inJail = false;
				setPlayerStartPosition();
			}
		}
		
		if(freeze_stay_timer > 0) {
			setVelocity(new Vector(0.f, 0.f));
			freeze_stay_timer -= delta;
		}
		
		if((input.isKeyDown(Input.KEY_W) || input.isKeyDown(Input.KEY_UP)) && freeze_stay_timer <= 0){
			setVelocity(new Vector(0.f, -EmpireWars.PLAYER_SPEED));
			changeDirection(Direction.UP, ew);
		}
		if((input.isKeyDown(Input.KEY_S) || input.isKeyDown(Input.KEY_DOWN)) && freeze_stay_timer <= 0){
			setVelocity(new Vector(0.f, EmpireWars.PLAYER_SPEED));
			changeDirection(Direction.DOWN, ew);
		}
		if((input.isKeyDown(Input.KEY_A) || input.isKeyDown(Input.KEY_LEFT)) && freeze_stay_timer <= 0){
			setVelocity(new Vector(-EmpireWars.PLAYER_SPEED, 0.f));
			changeDirection(Direction.LEFT, ew);
		}
		if((input.isKeyDown(Input.KEY_D) || input.isKeyDown(Input.KEY_RIGHT)) && freeze_stay_timer <= 0){
			setVelocity(new Vector(EmpireWars.PLAYER_SPEED, 0.f));
			changeDirection(Direction.RIGHT, ew);
		}
		if(!input.isKeyDown(Input.KEY_W) && !input.isKeyDown(Input.KEY_UP) 
				&& !input.isKeyDown(Input.KEY_S) && !input.isKeyDown(Input.KEY_DOWN)
				&& !input.isKeyDown(Input.KEY_A) && !input.isKeyDown(Input.KEY_LEFT)
				&& !input.isKeyDown(Input.KEY_D) && !input.isKeyDown(Input.KEY_RIGHT)){
			setVelocity(new Vector(0.f, 0.f));
		}
		
		translate(velocity.scale(delta));
		
		if(velocity.getY() < 0 ){
			direction = Direction.UP;
		}else if(velocity.getY()>0){
			direction = Direction.DOWN;
		}else if(velocity.getX()<0){
			direction = Direction.LEFT;
		}else if(velocity.getX()>0){
			direction = Direction.RIGHT;
		}
		
		// player shooting star bullets
		if(input.isKeyPressed(Input.KEY_B) && freeze_stay_timer <= 0 && ew.getStarBullets() > 0){
			shoot(ew,1);
			ew.setStarBullets(ew.getStarBullets() - 1);
		}
		// player shooting bullets
		if(input.isKeyPressed(Input.KEY_J) && freeze_stay_timer <= 0){
			shoot(ew,0);
		}

		// update health bar pos
		this.setHealthBarPos();
		
		int wallIndex = ew.map.getLayerIndex("walls");
		int minX = (int)(this.getCoarseGrainedMinX()/32);
		int minY = (int)(this.getCoarseGrainedMinY()/32);
		int maxX = (int)(this.getCoarseGrainedMaxX()/32);
		int maxY = (int)(this.getCoarseGrainedMaxY()/32);
		
		if (ew.map.getTileId(minX, minY, wallIndex) != 0 ||
				ew.map.getTileId(minX, maxY, wallIndex) != 0 ||
						ew.map.getTileId(maxX, minY, wallIndex) != 0 ||
								ew.map.getTileId(maxX, maxY, wallIndex) != 0)
		{
			this.setPosition(previousPoition);
			this.setHealthBarPos();
		}
		
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			Bullet bullet = i.next().getValue();
			if (bullet.collides(this) != null && !bullet.isDestroyed()) {
				if (bullet.team != this.team) {
					if (ew.getSessionType().equals("SERVER")) {
						ew.getScore().addScore(EmpireWars.PLAYER_KILL_POINTS, bullet.team);
					} else {
						ew.getScore().serverSendScoreUpdate(ew, EmpireWars.PLAYER_KILL_POINTS, bullet.team);
					}
					this.health.setHealth(-4);
					bullet.explode();
				}
				if (!bullet.getObjectType().equals("ORIGINAL")) {
					bullet.serverSendCollisionUpdates(ew);
				}
			}
		}
	}
	
	/**
	 * Update the health bar based on the players movement.
	 *
	 */
	public void setHealthBarPos() {
		this.health.setPosition(this.getX() - hbXOffset,  this.getY() - hbYOffset);
	}

	/**
	 * Draws all boundaries and images associated with the entity at their
	 * designated offset values. We override this so we can be able to debug the paths
	 * @param g The current graphics context
	 */
	@Override
	public void render(final Graphics g) {
		this.health.render(g);
		super.render(g);
	}
	
	public Vector getTileIdx(Vector v){
		return new Vector(v.getX()/32, v.getY()/32);
	}
	
	public void setTilePosition(Vector v) {
		this.tilePosition = v;
	}
}
