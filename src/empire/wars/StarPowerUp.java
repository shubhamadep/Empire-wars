package empire.wars;

public class StarPowerUp extends PowerUp {

	public StarPowerUp(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.STAR_RSC);
	}

	@Override
	public void handleServer(Player player) {
		this.game.setStarBullets(this.game.getStarBullets() + 1);
	}
}
