package empire.wars;

public class BananaPowerUp extends PowerUp {
	public final static Double incrementValue = 8.0;
	
	public BananaPowerUp(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.BANANA_POWERUP_RSC);
	}

	@Override
	public void handleServer(Player player) {
		player.health.setHealth(BananaPowerUp.incrementValue);
	}
}
