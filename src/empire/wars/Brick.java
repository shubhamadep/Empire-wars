package empire.wars;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;

public class Brick extends NetworkEntity {
	private Vector velocity;
	private BRICK_TYPE brick_type;
	
	public enum BRICK_TYPE
	{
		BRICK,
		GRASS,
		WATER
	}

	public Brick(final float x, final float y, final float vx, final float vy, final BRICK_TYPE in_brick_type){
		super(x,y);
		this.velocity = new Vector(vx, vy);
		this.brick_type = in_brick_type;
		
		//TODO: addImageWithBoundingBox(ResourceManager.getImage(this.bullet_image));
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta,
			int mapWidth, int mapHeight, int tileWidth, int tileHeight){
		translate(velocity.scale(delta));
	}
	
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
}