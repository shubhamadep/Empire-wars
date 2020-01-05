package empire.wars;

import java.awt.Font;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import empire.wars.net.Message;


public class PlayState extends BasicGameState {
	
	public int game_timer;
	private TrueTypeFont ttf;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		Font font = new Font("Comic Sans MS", Font.PLAIN, 16);
		ttf = new TrueTypeFont(font, false);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		EmpireWars ew = (EmpireWars)game;
		game_timer = EmpireWars.GAME_DURATION;
		if (ew.getSessionType().equals("SERVER")) {
			ew.createOnServer();
		}
		ew.createOnClients();
		ew.setLives(EmpireWars.MAX_LIVES);
		ew.setFreeCards(0);
		ew.setStarBullets(0);
		ew.getScore().setBlueTeam(0);
		ew.getScore().setRedTeam(0);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		EmpireWars ew = (EmpireWars) game;
		ew.camera.translate(g, ew.player);
		ew.map.render(0, 0);
		if (!ew.player.isDestroyed()) {
			ew.player.render(g);
		}
		
		
		for (Iterator<HashMap.Entry<UUID, Castle>> i = ew.castles.entrySet().iterator(); i.hasNext(); ) {
			HashMap.Entry<UUID, Castle> itr = i.next();
			itr.getValue().render(g);
		}

		/*for (Iterator<HashMap.Entry<UUID, CastleFire>> i = ew.clientCastleFires.entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}*/
		
		for (Iterator<HashMap.Entry<UUID, Flag>> i = ew.getFlags().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}

		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			HashMap.Entry<UUID, Creep> itr = i.next();
			itr.getValue().render(g);
		}

		for (Iterator<HashMap.Entry<UUID, Player>> i = ew.getClientPlayer().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}

		for (Iterator<HashMap.Entry<UUID, HeartPowerUp>> i = ew.getHeartPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, VanishingAct>> i = ew.getVanishPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, ShieldFlags>> i = ew.getShieldPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, StarPowerUp>> i = ew.getStarPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, BananaPowerUp>> i = ew.getBananaPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		for (Iterator<HashMap.Entry<UUID, FreezePowerUp>> i = ew.getFreezePowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		for (Iterator<HashMap.Entry<UUID, KeyPowerUp>> i = ew.getKeyPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}
		
		// bullets
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().render(g);
		}

		g.setFont(ttf);
		g.setColor(Color.black);
		g.fillRect((-1 * ew.camera.getXIndicator()),(-1 * ew.camera.getYIndicator()),EmpireWars.SCREEN_WIDTH,35);
		g.setColor(Color.white);
		g.drawString(
				"Time Left: "+ (game_timer) / 60000 + ":" + ((game_timer) % 60000 / 1000) ,
				(-1 * ew.camera.getXIndicator() + 440), (-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.green);
		g.drawString("Lives: "  + ew.getLives(), (-1 * ew.camera.getXIndicator() + 20),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.yellow);
		g.drawString("Free Cards: "  + ew.getFreeCards(), (-1 * ew.camera.getXIndicator() + 120),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.magenta);
		g.drawString("BAZUKA: "  + ew.getStarBullets(), (-1 * ew.camera.getXIndicator() + 700),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.red);
		g.drawString(
			"Red: "  + ew.getScore().getRedTeam(), (-1 * ew.camera.getXIndicator() + 830),(-1 * ew.camera.getYIndicator()) + 10);
		g.setColor(Color.blue);
		g.drawString(
			"Blue: "  + ew.getScore().getBlueTeam(), (-1 * ew.camera.getXIndicator() + 930),(-1 * ew.camera.getYIndicator()) + 10);
	}
	

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		EmpireWars ew = (EmpireWars) game;
		game_timer -= delta;
		
		if (game_timer <= 0 && ew.getSessionType().equals("SERVER")) {
			Message msg = new Message("", "END"); 
			ew.appendSendPackets(msg);
			game.enterState(EmpireWars.GAMEOVERSTATE_ID);
		}
		
		if (ew.player != null) {
			ew.player.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		}
		
		for (Iterator<HashMap.Entry<UUID, Castle>> i = ew.castles.entrySet().iterator(); i.hasNext(); ) {
			Castle castle = i.next().getValue();
			castle.update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		}
		
		/*for (Iterator<HashMap.Entry<UUID, CastleFire>> i = ew.clientCastleFires.entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		}*/

		ew.getScore().update(game);
		
		for (Iterator<HashMap.Entry<UUID, Creep>> i = ew.creeps.entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(game, delta);
		}
		
		for (Iterator<HashMap.Entry<UUID, HeartPowerUp>> i = ew.getHeartPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, StarPowerUp>> i = ew.getStarPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, VanishingAct>> i = ew.getVanishPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, ShieldFlags>> i = ew.getShieldPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, BananaPowerUp>> i = ew.getBananaPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, FreezePowerUp>> i = ew.getFreezePowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		for (Iterator<HashMap.Entry<UUID, KeyPowerUp>> i = ew.getKeyPowerup().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update();
		}
		
		for (Iterator<HashMap.Entry<UUID, Bullet>> i = ew.getClientBullets().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(container, game, delta, ew.mapWidth, ew.mapHeight, ew.tileWidth, ew.tileHeight);
		}
		
		for (Iterator<HashMap.Entry<UUID, Flag>> i = ew.getFlags().entrySet().iterator(); i.hasNext(); ) {
			i.next().getValue().update(container, game, delta);
		}
					
		// process network message
		for (Iterator<Message> i = ew.receivedPackets.iterator(); i.hasNext(); ) {
			Message.determineHandler(i.next(), ew);
			i.remove();
		}
				
		// remove bullets
		HashMap<UUID, Bullet> bulletMap = ew.getClientBullets();
		bulletMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove heart power-up
		HashMap<UUID, HeartPowerUp> heartPowerup = ew.getHeartPowerup();
		heartPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove star power-up
		HashMap<UUID, StarPowerUp> starPowerup = ew.getStarPowerup();
		starPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove heart power-up
		HashMap<UUID, VanishingAct> vanishPowerup = ew.getVanishPowerup();
		vanishPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		//remove shield power-up
		HashMap<UUID, ShieldFlags> shieldFlagPowerup = ew.getShieldPowerup();
		shieldFlagPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove banana power-up
		HashMap<UUID, BananaPowerUp> bananaPowerup = ew.getBananaPowerup();
		bananaPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		
		// remove freeze power-up
		HashMap<UUID, FreezePowerUp> freezePowerup = ew.getFreezePowerup();
		freezePowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove heart power-up
		HashMap<UUID, KeyPowerUp> keyPowerup = ew.getKeyPowerup();
		keyPowerup.entrySet().removeIf(entry->entry.getValue().isExploded() == true);		
		// remove creep 
		HashMap<UUID, Creep> creepMap = ew.getCreeps();
		creepMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
		// remove client player 
		HashMap<UUID, Player> playerMap = ew.getClientPlayer();
		playerMap.entrySet().removeIf(entry->entry.getValue().isExploded() == true);
	}

	@Override
	public int getID() {
		return EmpireWars.PLAY_STATE_ID;
	}

}
