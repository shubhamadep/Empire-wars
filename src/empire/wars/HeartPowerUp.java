package empire.wars;

/*
 * Implement the heart power-up
 */
public class HeartPowerUp extends PowerUp {

	public HeartPowerUp(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.HEART_POWERUP_RSC);
	}

	@Override
	public void handleServer(Player player) {
		this.game.setLives(this.game.getLives() + 1);
	}

}
