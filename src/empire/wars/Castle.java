package empire.wars;

import java.util.Random;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.Bullet.BULLET_TYPE;
import empire.wars.EmpireWars.TEAM;
import jig.ResourceManager;


public class Castle extends NetworkEntity {
	int timer;
	Random rand;
	
	public Castle(final float x, final float y, final TEAM in_team){
		super(x,y);
		this.team = in_team;
		this.rand = new Random();
		
		if (in_team == TEAM.BLUE)
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_BLUE_RSC));
		else
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_RED_RSC));
	}
	
	public void changeColor(TEAM team) {
		removeImage(ResourceManager.getImage(EmpireWars.CASTLE_RED_RSC));
		removeImage(ResourceManager.getImage(EmpireWars.CASTLE_BLUE_RSC));
		this.team = team;
		if (team == TEAM.BLUE)
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_BLUE_RSC));
		else
			addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.CASTLE_RED_RSC));
	}
	
	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
	}
	
	
	public void update(GameContainer container, StateBasedGame game, int delta, int mapWidth, int mapHeight, int tileWidth, int tileHeight)
	{
		EmpireWars ew = (EmpireWars) game;
		this.networkUpdate(ew);  // network updates
		if (!ew.getSessionType().equals("SERVER")) {
			return;
		}
		timer += delta;
		if (timer > 4000) {
			float vx = this.rand.nextFloat() * (0.2f);
			if (rand.nextInt() % 2 == 0)
				vx = -vx;
			
			float vy = (this.team == TEAM.BLUE) ? -0.1f : 0.1f;
			
			Bullet bullet = new Bullet(getX(), getY(), vx, vy, EmpireWars.FIRE_IMAGE_RSC, BULLET_TYPE.CASTLE, this.team, 2);
			ew.clientBullets.put(bullet.getObjectUUID(), bullet);
			this.timer = 0;
		}
	}
}