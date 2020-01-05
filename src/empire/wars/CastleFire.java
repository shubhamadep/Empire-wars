/*package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;
import jig.Vector;
import jig.ResourceManager;

public class CastleFire extends NetworkEntity {
	private Vector velocity;
	public TEAM team;
	public float start_x, start_y;
	
	public CastleFire(final float x, final float y, final float vx, final float vy, final TEAM in_team){
		super(x,y);
		start_x = x;
		start_y = y;
		this.velocity = new Vector(vx, vy);
		this.team = in_team;
		
		addImageWithBoundingBox(ResourceManager.getImage(EmpireWars.FIRE_IMAGE_RSC));
	}

	public void serverSendCollisionUpdates(EmpireWars game) {
		String className = this.getClass().getSimpleName().toUpperCase();
		Message posUpdate = new Message(
			this.getObjectUUID(), "UPDATE", "SETSERVERCOL", "", className);
		game.sendPackets.add(posUpdate);
	}

	
	private void checkCollision(EmpireWars ew) {
		if (!this.objectType.equals("ORIGINAL")) {
			return;
		}
		int bx = (int) this.getX() / 32;
		int by = (int) this.getY() / 32;
		int wallIndex = ew.map.getLayerIndex("walls");
		if (ew.map.getTileId(bx, by, wallIndex) != 0 ) {
			this.explode();
		}
	}


	public void changeColor(TEAM team) {
		this.team = team;
	}


	@Override
	public void networkUpdate(EmpireWars game) {
		super.networkUpdate(game);
		this.sendColorUpdate(game);
	}


	public void update(GameContainer container, StateBasedGame game, int delta, int mapWidth, int mapHeight, int tileWidth, int tileHeight)
	{
		if (Math.sqrt(((int)this.start_x - (int)getX())^2 + (((int)this.start_y - (int)getY()))^2) > 50) // if euclidean distance exceeds 50, then fire loses its ability
		{
			this.explode();
		}
		else if (!this.isExploded())
		{
			EmpireWars ew = (EmpireWars)game;
			if (this.team != ew.player.team && this.collides(ew.player) != null)
			{
				this.explode();
				System.out.println("calling");
				ew.player.health.setHealth(-4);
				removeImage(ResourceManager.getImage(EmpireWars.FIRE_IMAGE_RSC));
				return;
			}
			else
			{
				translate(velocity.scale(delta));
			}
		}
//		this.checkCollision(ew);
//		this.networkUpdate(ew);  // network updates
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
}
*/