package empire.wars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import empire.wars.EmpireWars.Direction;
import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.Collision;
import jig.ResourceManager;
import jig.Vector;

public class Creep extends NetworkEntity {
	private Vector velocity;
	private UUID playerUUID = null; 
	private Direction direction;
	private Direction _direction;
	public HealthBar health;
	public float hbXOffset = 16; // health bar offset so its on top of the players head
	public float hbYOffset = 25; // health bar offset so its on top of the players head
	public int unitNumber = -1;
	public static int blueUnitNumber = 0;
	public static int redUnitNumber = 0;
	public static int targetDistance = 16;
	public Boolean currentlyCreeping = false;
	int wallLayer, roadLayer;
	public static Vector[] targetLocations = {new Vector(-targetDistance,0), new Vector(-targetDistance,targetDistance), new Vector(0,targetDistance), new Vector(targetDistance,targetDistance), new Vector(targetDistance,0), new Vector(targetDistance,-targetDistance), new Vector(0,-targetDistance), new Vector(-targetDistance,-targetDistance)};	
	PathFinder pathFinder;
	
	Random rand = new Random();
	int freeze_time = 0;
	final float CREEP_SPEED = 0.1f;
	
	public static ArrayList<Vector> speedVectors = new ArrayList<Vector>() {{
		add(new Vector(0f,-0.1f));
		add(new Vector(0.1f,0f));
		add(new Vector(0f,0.1f));
		add(new Vector(-0.1f,0f));
	}};
	
	private Animation creep_movement_up = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 3, 2, 3, true, 150, true);
	private Animation creep_movement_down = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 0, 2, 0, true, 150, true);
	private Animation creep_movement_left = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 1, 2, 1, true, 150, true);
	private Animation creep_movement_right = new Animation(ResourceManager.getSpriteSheet(
			EmpireWars.CREEP_MOVING_IMG_RSC, 48, 48), 0, 2, 2, 2, true, 150, true);

	public Creep(final float x, final float y, final TEAM in_team, final TiledMap in_map){
		super(x,y);
		int randNumber = rand.nextInt(4);
		direction = Direction.values()[randNumber];
		this.team = in_team;
		this.health = new HealthBar(this.getX() - hbXOffset,  this.getY() - hbYOffset, in_team);
		this.pathFinder = new PathFinder(x,y,in_team, in_map);
		this.wallLayer = in_map.getLayerIndex("walls");
		this.roadLayer = in_map.getLayerIndex("road");
		if (in_team == TEAM.BLUE)
		{
			this.unitNumber = Creep.blueUnitNumber;
			Creep.blueUnitNumber += 1;
		}
		else
		{
			this.unitNumber = Creep.redUnitNumber;
			Creep.redUnitNumber += 1;
		}
		
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.PLAYER_IMG_RSC));
		addAnimation(getAnimation(direction));
		setVelocity(speedVectors.get(direction.ordinal()));
	}
	
	public Animation getAnimation(Direction direction)
	{

		switch (direction)
		{
		case UP:
			return creep_movement_up;
		case DOWN:
			return creep_movement_down;
		case LEFT:
			return creep_movement_left;
		case RIGHT:
			return creep_movement_right;
		default:
			return creep_movement_down;
		}
	}
	
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
	 * player uuid setter.
	 * 
	 * @param uuid. A uuid that belongs to a client player.
	 * 
	 */
	public void setPlayerUUID(UUID uuid) {
		this.playerUUID = uuid;
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
		this.sendHealthBarUpdates(game);
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
		addAnimation(getAnimation(this.direction));
	}
	
	public void changeDirection(Direction new_direction, EmpireWars game)
	{
		removeAnimation(getAnimation(Direction.UP));
		removeAnimation(getAnimation(Direction.DOWN));
		removeAnimation(getAnimation(Direction.LEFT));
		removeAnimation(getAnimation(Direction.RIGHT));
		addAnimation(getAnimation(new_direction));
		direction = new_direction;
		setVelocity(speedVectors.get(new_direction.ordinal()));
		this.sendDirectionUpdates(game, "SETDIRECTION");
	}
	
	public void changeDirectionUsingVelocity(Vector speedVec, EmpireWars ew)
	{
		for(int i=0; i<speedVectors.size(); i++)
		{
			if (speedVectors.get(i).getX() == speedVec.getX() && speedVectors.get(i).getY() == speedVec.getY())
			{
				changeDirection(Direction.values()[i], ew);
			}
		}
	}
	
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
	 * Allows the server to send health updates to the client that owns that player.
	 * 
	 * @param x. How much life to reduce
	 * @param game. Current game state
	 * @param player. The entity to update the health bar
	 */
	private void sendPlayerHealth(EmpireWars game, double x, Player player) {
		String className = this.getClass().getSimpleName().toUpperCase();
		// only this player should process it
		String msg = Double.toString(x);
		Message msgUpdate = new Message(
			this.getObjectUUID(), "UPDATE", "SETPLAYERLIFE", msg, className);
		msgUpdate.setSingleClient(true);
		msgUpdate.setPort(player.getPort());
		msgUpdate.setIpAddress(player.getIpAddress());
		game.sendPackets.add(msgUpdate);
	};
	
	public void bounce(float surfaceTangent) {
		velocity = velocity.bounce(surfaceTangent);
	}
	
	/*
	 * return the player to plan a path to.
	 */
	private Player getAttackPlayer(EmpireWars ew) {
		if (this.playerUUID != null) {
			return ew.getClientPlayer().get(this.playerUUID);
		}		
		return ew.player;
	}
	
	public void update(StateBasedGame game, int delta){
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		
		// run this code on the server only 
		if (!ew.getSessionType().equals("SERVER")) {
			return;
		}
		
		this.freeze_time++;
		
		//if the creep belongs to opponent and if a path has not already been constructed, find a path to opponent player
		Player attackPlayer = this.getAttackPlayer(ew); 
		if (this.team != attackPlayer.team && !attackPlayer.inJail && !this.currentlyCreeping && (this.pathFinder.pathStack == null || this.pathFinder.pathStack.isEmpty()))
		{
			Vector playerPos = attackPlayer.getPosition();
			Vector targetPos = new Vector(playerPos.getX() + Creep.targetLocations[this.unitNumber%8].getX(), playerPos.getY() + Creep.targetLocations[this.unitNumber%8].getY());
			this.pathFinder.findPath(getPosition(), targetPos);
		}
		
		if (this.team != attackPlayer.team && attackPlayer.inJail)
		{
			this.pathFinder.pathStack.clear();
		}
		
		float vx = 0, vy = 0;

		while (!this.pathFinder.pathStack.isEmpty())
		{
			Node node = (Node) this.pathFinder.pathStack.peek();
			Vector pos = EmpireWars.tile2pos(new Vector(node.x, node.y));
			
			if(Math.abs(getX() - pos.getX()) >= 5)
			{
				if(pos.getX() - getX() > 0)
				{
					vx = CREEP_SPEED;
				}
				else if(pos.getX() - getX() < 0)
				{
					vx = -CREEP_SPEED;
				}
				else
				{
					vx = 0;
				}
				changeDirectionUsingVelocity(new Vector(vx, vy), ew);
				break;
			}
			else if(Math.abs(getY() - pos.getY()) >= 5)
			{
				if(pos.getY() - getY() > 0)
				{
					vy = CREEP_SPEED;
				}
				else if(pos.getY() - getY() < 0)
				{
					vy = -CREEP_SPEED;
				}
				else
				{
					vy = 0;
				}
				changeDirectionUsingVelocity(new Vector(vx, vy), ew);
				break;
			}
			else
			{
				this.pathFinder.pathStack.pop();
				Vector playerTile = EmpireWars.getTileIdx(attackPlayer.getPosition());
				// recompute path if player has moved
				if(playerTile.getX() != attackPlayer.tilePosition.getX() || playerTile.getY() != attackPlayer.tilePosition.getY())
				{
					attackPlayer.setTilePosition(playerTile);
					
					Vector playerPos = attackPlayer.getPosition();
					Vector targetPos = new Vector(playerPos.getX() + Creep.targetLocations[this.unitNumber%8].getX(), playerPos.getY() + Creep.targetLocations[this.unitNumber%8].getY());
					this.pathFinder.findPath(getPosition(), targetPos);
				}
				break;
			}
		}

		if (this.health.isDead()) {
			this.explode();
			// the entity is dead. No need to go on
			return;
		}

		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			Bullet bullet = i.next().getValue();
			if (bullet.collides(this) != null && !bullet.isDestroyed()) {
				if (bullet.team != this.team) {
					if(bullet.getBullet() == 1) {
						ew.getScore().addScore(EmpireWars.KILL_POINTS, bullet.team);
						this.health.setHealth(-16);
					} else {
						ew.getScore().addScore(EmpireWars.KILL_POINTS, bullet.team);
						this.health.setHealth(-2);
					}
				}
				if (!bullet.getObjectType().equals("ORIGINAL")) {
					bullet.serverSendCollisionUpdates(ew);
				}
				bullet.explode();
			}
		}

		// server player
		boolean collideServerPlayer = false;
		if (this.collides(ew.player) != null) {
			if (this.team != ew.player.team)
			{
				ew.player.health.setHealth(-0.02);
				this.setVelocity(new Vector(0f,0f));
				this.pathFinder.pathStack.clear();
				this.currentlyCreeping = true;
				collideServerPlayer = true;
			}
		}
		else
		{
			this.currentlyCreeping = false;
			if (this.getVelocity().getX() == 0f && this.getVelocity().getY() == 0f)
				setVelocity(speedVectors.get(this.direction.ordinal()));
		}
		
		// network players
		if (!collideServerPlayer) {
			for (Iterator<HashMap.Entry<UUID, Player>> i = ew.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
				Player tempPlayer = i.next().getValue();
				Collision isPen = this.collides(tempPlayer);
	
				if (isPen != null) {
					if (this.team != tempPlayer.team)
					{
						this.sendPlayerHealth(ew, -0.02, tempPlayer);
						this.setVelocity(new Vector(0f,0f));
						this.pathFinder.pathStack.clear();
						this.currentlyCreeping = true;
					}
				} else {
					this.currentlyCreeping = false;
					if (this.getVelocity().getX() == 0f && this.getVelocity().getY() == 0f) {
						setVelocity(speedVectors.get(this.direction.ordinal()));
					}
				}
			}
		}
		

		for(Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext();){
			HashMap.Entry<UUID, Creep> itr = i.next();
			if (this.getObjectUUID() == itr.getValue().getObjectUUID())
				continue;
				
			if (this.collides(itr.getValue()) != null)
			{
				if (this.team != itr.getValue().team)
				{
					this.health.setHealth(-0.1);
					itr.getValue().health.setHealth(-0.1);
					this.setVelocity(new Vector(0,0)); //in case of creeps colliding with enemy creeps, they fight until one of them loses all its health
				}
				else
				{
					if (this.currentlyCreeping == true || itr.getValue().currentlyCreeping == true)
					{
						itr.getValue().setVelocity(new Vector(0,0));
						this.setVelocity(new Vector(0,0));
					}
				}
			}		
		}

		int minX, minY, maxX, maxY;
		
		minX = (int)(this.getCoarseGrainedMinX()/32);
		minY = (int)(this.getCoarseGrainedMinY()/32);
		maxX = (int)(this.getCoarseGrainedMaxX()/32);
		maxY = (int)(this.getCoarseGrainedMaxY()/32);
		
		Direction new_direction = this.direction;
		
		if (this.getCoarseGrainedMinX() < 64)
		{
			setX(ew.tileWidth*2.7f);
			minX = 2;
			new_direction = Direction.RIGHT;
			changeDirection(new_direction, ew);
		}
		else if (this.getCoarseGrainedMinY() < 64)
		{
			setY(ew.tileHeight*2.7f);
			minY = 2;
			new_direction = Direction.DOWN;
			changeDirection(new_direction, ew);
		}
		else if (this.getCoarseGrainedMaxX() > ew.mapWidth-64)
		{
			setX(ew.mapWidth - ew.tileWidth*2.7f);
			maxX = (int)(getX()/32.0f);
			new_direction = Direction.LEFT;
			changeDirection(new_direction, ew);
		}
		else if (this.getCoarseGrainedMaxY() > ew.mapHeight-64)
		{
			setY(ew.mapHeight - ew.tileHeight*2.7f);
			maxY = (int)(getY()/32.0f);
			new_direction = Direction.UP;
			changeDirection(new_direction, ew);
		}
		
		try
		{
			Vector newPosition = new Vector(getX() + velocity.getX() * delta, getY() + velocity.getY() * delta);
			if(!EmpireWars.collidesWithWalls(newPosition, ew.map, wallLayer))
			{
				translate(velocity.scale(delta));
				freeze_time = 0;
			}
			else
			{	translate(velocity.scale(0.5f*delta));
				minX = (int)(newPosition.getX() - 16) / 32;
				minY = (int)(newPosition.getY() - 16) / 32;
				maxX = (int)(newPosition.getX() + 16) / 32;
				maxY = (int)(newPosition.getY() + 16) / 32;

				if (ew.map.getTileId(minX, minY, wallLayer) != 0 && ew.map.getTileId(maxX, minY, wallLayer) != 0)
				{
					new_direction = Direction.DOWN;
				}
				else if (ew.map.getTileId(minX, maxY, wallLayer) != 0 && ew.map.getTileId(minX, minY, wallLayer) != 0)
				{
					new_direction = Direction.RIGHT;
				}
				else if (ew.map.getTileId(minX, maxY, wallLayer) != 0 && ew.map.getTileId(maxX, maxY, wallLayer) != 0)
				{
					new_direction = Direction.UP;
				}
				else if (ew.map.getTileId(maxX, minY, wallLayer) != 0 && ew.map.getTileId(maxX, maxY, wallLayer) != 0)
				{
					new_direction = Direction.LEFT;
				}
				else if (ew.map.getTileId(minX, minY, wallLayer) != 0) // top left corner stuck
				{
					if (ew.map.getTileId(minX, maxY+1, wallLayer) == 0)
						new_direction = Direction.DOWN;
					else
						new_direction = Direction.RIGHT;
				}
				else if (ew.map.getTileId(maxX, minY, wallLayer) != 0)
				{
					if (ew.map.getTileId(minX-1, minY, wallLayer) == 0)
						new_direction = Direction.LEFT;
					else
						new_direction = Direction.DOWN;
				}
				else if (ew.map.getTileId(minX, maxY, wallLayer) != 0)
				{
					if (ew.map.getTileId(minX, minY-1, wallLayer) == 0)
						new_direction = Direction.UP;
					else
						new_direction = Direction.RIGHT;
				}
				else if (ew.map.getTileId(maxX, maxY, wallLayer) != 0)
				{
					if (ew.map.getTileId(minX-1, maxY, wallLayer) == 0)
						new_direction = Direction.LEFT;
					else
						new_direction = Direction.UP;
				}
				else
				{
					new_direction = Direction.values()[(this.direction.ordinal() + 2)%4]; //always reverse when hitting a wall
				}
				if (new_direction != this.direction)
					changeDirection(new_direction, ew);
			}
		}
		catch(Exception e)
		{
			this.explode();
		}
		this.setHealthBarPos();
	}
	
	/**
	 * Update the health bar based on the creeps movement.
	 *
	 */
	public void setHealthBarPos() {
		this.health.setPosition(this.getX() - hbXOffset,  this.getY() - hbYOffset);
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Draws all boundaries and images associated with the entity at their
	 * designated offset values. We override this so we can be able to debug the paths
	 * @param g The current graphics context
	 */
	@Override
	public void render(final Graphics g) {
		super.render(g);
		this.health.render(g);
	}
}


/* TODO - remove if not necessary by the end
if (this.freeze_time > 50 && this.currentlyCreeping != true) 
{
	int minX = (int)(getX() - 16) / 32;
	int minY = (int)(getY() - 16) / 32;
	int maxX = (int)(getX() + 16) / 32;
	int maxY = (int)(getY() + 16) / 32;
	Direction new_direction;
	
	if (ew.map.getTileId(minX, minY, wallLayer) != 0 && ew.map.getTileId(maxX, minY, wallLayer) != 0)
	{
		new_direction = Direction.DOWN;
		this.setY(this.getY() + 10);
	}
	else if (ew.map.getTileId(minX, maxY, wallLayer) != 0 && ew.map.getTileId(minX, minY, wallLayer) != 0)
	{
		new_direction = Direction.RIGHT;
		this.setX(this.getX() + 10);
	}
	else if (ew.map.getTileId(minX, maxY, wallLayer) != 0 && ew.map.getTileId(maxX, maxY, wallLayer) != 0)
	{
		new_direction = Direction.UP;
		this.setY(this.getY() - 10);
	}
	else if (ew.map.getTileId(maxX, minY, wallLayer) != 0 && ew.map.getTileId(maxX, maxY, wallLayer) != 0)
	{
		new_direction = Direction.LEFT;
		this.setX(this.getX() - 10);
	}
	else if (ew.map.getTileId(minX, minY, wallLayer) != 0) // top left corner stuck
	{
		this.setX(this.getX() + 10);
		this.setY(this.getY() + 10);
		if (ew.map.getTileId(minX, maxY+1, wallLayer) == 0)
			new_direction = Direction.DOWN;
		else
			new_direction = Direction.RIGHT;
	}
	else if (ew.map.getTileId(maxX, minY, wallLayer) != 0)
	{
		this.setX(this.getX() - 10);
		this.setY(this.getY() + 10);
		if (ew.map.getTileId(minX-1, minY, wallLayer) == 0)
			new_direction = Direction.LEFT;
		else
			new_direction = Direction.DOWN;
	}
	else if (ew.map.getTileId(minX, maxY, wallLayer) != 0)
	{
		this.setX(this.getX() + 10);
		this.setY(this.getY() - 10);
		if (ew.map.getTileId(minX, minY-1, wallLayer) == 0)
			new_direction = Direction.UP;
		else
			new_direction = Direction.RIGHT;
	}
	else if (ew.map.getTileId(maxX, maxY, wallLayer) != 0)
	{
		this.setX(this.getX() - 10);
		this.setY(this.getY() - 10);
		if (ew.map.getTileId(minX-1, maxY, wallLayer) == 0)
			new_direction = Direction.LEFT;
		else
			new_direction = Direction.UP;
	}
	else
	{
		new_direction = Direction.values()[this.rand.nextInt(4)];
		switch(new_direction)
		{
		case UP:
			this.setY(this.getY() - 10);
			break;
		case DOWN:
			this.setY(this.getY() + 10);
			break;
		case RIGHT:
			this.setX(this.getX() + 10);
			break;
		case LEFT:
			this.setX(this.getX() - 10);
			break;
		}
	}
	if (new_direction != this.direction)
		changeDirection(new_direction, ew);
	
	this.freeze_time = 0;
	this.changeDirection(new_direction, ew);
}*/