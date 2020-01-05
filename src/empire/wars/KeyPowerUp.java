package empire.wars;

public class KeyPowerUp extends PowerUp {

	public KeyPowerUp(float x, float y, EmpireWars game) {
		super(x, y, game, EmpireWars.KEY_RSC);
	}
	
	@Override
	public void handleServer(Player player) {
		this.game.setFreeCards(this.game.getFreeCards() + 1);
	}
}
