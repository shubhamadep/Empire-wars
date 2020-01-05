package empire.wars;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;
import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.ResourceManager;
import jig.Vector;

public class Flag extends NetworkEntity {
	public int player_stay_timer = 3000; // player has to stay for 3 sec to change the color
	public int shield_stay_timer = 0; // sheilds flags for 10 seconds
	public Vector flagTileIdx;
	
	private int vanishTime = 0;
	
	boolean isShielded = false;
	
	public Flag(final float x, final float y){
		super(x, y);
		flagTileIdx = new Vector(x/32, y/32);
		team = TEAM.GREY;
		_team = TEAM.GREY;
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
	}
	
	public Flag(final float x, final float y, final TEAM team){
		super(x, y);
		flagTileIdx = new Vector(x/32, y/32);
		this.team = team;
		if(team == TEAM.BLUE){
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		}else if(team == TEAM.RED){
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		}else{
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
		}
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		this.flagTileIdx = new Vector(x / 32, y / 32);
	}
	
	/*
	 * Vanishtime getter
	 */
	public int getVanishTime() {
		return vanishTime;
	}
	
	/*
	 * Vanishtime setter
	 */
	public void setVanishTime(int vanishTime) {
		this.vanishTime = vanishTime;
	}
	
	/*
	 * shieldedtime setter
	 */
	public void setShieldedTime(int shield_stay_timer) {
		this.shield_stay_timer = shield_stay_timer;
	}
	

	public void changeTeam(TEAM team) {
		this.team = team;
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		if (team == TEAM.RED) {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		} else if(team == TEAM.BLUE) {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		}
		player_stay_timer = 3000;
	}
	
	public void shieldFlags() {
		removeImage(ResourceManager.getImage(EmpireWars.SHIELD_RED_FLAG));
		removeImage(ResourceManager.getImage(EmpireWars.SHIELD_BLUE_FLAG));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		if (this.team == TEAM.RED) {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.SHIELD_RED_FLAG));	
		} else {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.SHIELD_BLUE_FLAG));
		}
		this.isShielded = true;
	}
	
	private void removeShields() {
		removeImage(ResourceManager.getImage(EmpireWars.SHIELD_RED_FLAG));
		removeImage(ResourceManager.getImage(EmpireWars.SHIELD_BLUE_FLAG));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_GREYIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		if (this.team == TEAM.RED) {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_REDIMG_RSC));
		} else if (this.team == TEAM.BLUE) {
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FLAG_BLUEIMG_RSC));
		}	
		this.isShielded = false;
	}
	
	/*
	 * Update clients on the team color I belong to
	 */
	public void sendColorUpdate(EmpireWars game) {
		if (this.team != this._team) {
			String className = this.getClass().getSimpleName().toUpperCase();
			String msg = this.team.toString();
			Message posUpdate = new Message(
				this.getObjectUUID(), "UPDATE", "SETCOLOR", msg, className);
			game.sendPackets.add(posUpdate);
			this._team = this.team; 
		}
	}
	
	private Vector getTileIdx(Vector v){
		return new Vector(v.getX()/32, v.getY()/32);
	}
	
	private boolean playerIsNear(Vector playerTileIdx){
		for(int i=-1; i<2; i++){
			for(int j=-1; j<2; j++){
				if((int)playerTileIdx.getX() == (int)(flagTileIdx.getX()+i)
						&& (int)playerTileIdx.getY() == (int)(flagTileIdx.getY()+j)){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
	}
	
	
	public void update(GameContainer container, StateBasedGame game, final int delta) {
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		if (this.vanishTime > 0) {
			this.vanishTime -= delta;
			return;
		}
		
		//examine this flag's neighborhood to check if there is a player
		Vector playerTileIdx = getTileIdx(ew.player.getPosition());
		if (ew.player.team != team && playerIsNear(playerTileIdx) && this.shield_stay_timer <= 0) {
			player_stay_timer -= delta;
		} else {
			player_stay_timer = 3000;
		}
		
		//if the player has stayed for enough time, change the flag's color
		if (player_stay_timer <= 0) {
			ew.getScore().addScore(EmpireWars.CHANGE_FLAG_POINTS, ew.player.team);
			this.changeTeam(ew.player.team);
		}
		if(this.shield_stay_timer > 0) {
			this.shield_stay_timer -= delta;
			this.shieldFlags();
		} else if (this.isShielded && this.shield_stay_timer <= 0) {
			removeShields();			
		}
	}

	@Override
	public void render(final Graphics g) {
		if (this.vanishTime > 0) {
			return;
		}
		super.render(g);
		float x = this.getX() - 0;  
		float y = this.getY() - 25;
		if(player_stay_timer < 3000 && this.shield_stay_timer <= 0){
			g.setColor(Color.magenta);
			g.fillRect(x, y, 20.0f * player_stay_timer / 3000, 5);
		}
	}

	/*
	 * team getter
	 */
	public TEAM getTeam() {
		return this.team;
	}

}
