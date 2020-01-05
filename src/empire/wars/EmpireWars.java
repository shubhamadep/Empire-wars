package empire.wars;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import empire.wars.net.ConnectedPlayers;
import empire.wars.net.GameClient;
import empire.wars.net.GameServer;
import empire.wars.net.Message;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;


/**
 * Empire Wars is a 2D multi-player online battle arena video game. 
 * There are two teams in the game: Red team and the Blue team, and the 
 * objective is to acquire as many flags as possible in the field.
 * 
 * @author priya, peculiaryak, jun, shubham
 *
 */
public class EmpireWars extends StateBasedGame {
	public enum TEAM
	{
		GREY,
		RED,
		BLUE
	}
	
	public enum Direction
	{
	  UP, 
	  RIGHT, 
	  DOWN, 
	  LEFT
	}
	
	public final static int SPLASH_SCREEN_STATE_ID = 0;
	public final static int MENU_STATE_ID = 1;
	public final static int SESSION_STATE_ID = 2;
	public final static int SERVER_LOBBY_STATE_ID = 3;
	public final static int CLIENT_LOBBY_STATE_ID = 4;
	public final static int PLAY_STATE_ID = 5;
	public final static int GAMEOVERSTATE_ID = 6;
	
	public final static int KILL_POINTS = 2;
	public final static int PLAYER_KILL_POINTS = 5;
	public final static int CHANGE_FLAG_POINTS = 30;
	public final static int GAME_DURATION = 300000;
	public final static int MAX_LIVES = 3;
	public final static int POWERUP_COUNT = 3;
	public final static int PLAYER_CREEPS = 4;
	
	public final static int SCREEN_WIDTH = 1024;
	public final static int SCREEN_HEIGHT = 768;
	public final static int SCREEN_SMALL_WIDTH = 900;
	public final static int SCREEN_SMALL_HEIGHT = 600;
	public final static int SERVER_PORT = 1323;
	public final static int THREAD_SLEEP_TIME = 0;
	
	private GameClient socketClient;
	private GameServer socketServer;
	
	private String username;
	private int lives = EmpireWars.MAX_LIVES;
	private int freeCards = 0;
	private int bullets = 0;
	private Score score = new Score();
	
	public final static float PLAYER_SPEED = 0.40f;
	public final static float PLAYER_BULLETSPEED = 0.50f;
	
	public TiledMap map;
	Player player;
	Camera camera;
	int mapHeight, mapWidth;
	int tileHeight, tileWidth;
	
	//image resources
	public static final String PLAYER_IMG_RSC = "images/background.gif";
	public static final String PLAYER_MOVINGIMG_RSC = "images/jun.png";
	public static final String PLAYER_BULLETIMG_RSC = "images/player_bullet.gif";
	public static final String SPLASH_SCREEN_IMG_RSC = "images/splash.png";
	public static final String LOGO_IMG_RSC = "images/logo.png";
	public static final String MENU_BUTTONS_RSC = "images/menu_buttons.png";
	public static final String FLAG_GREYIMG_RSC = "images/grey_flag.png";
	public static final String FLAG_BLUEIMG_RSC = "images/blue_flag.png";
	public static final String FLAG_REDIMG_RSC = "images/red_flag.png";
	public static final String CREEP_MOVING_IMG_RSC = "images/creeps.png";
	public static final String HEART_POWERUP_RSC = "images/heartpowerup.png";
	public static final String BANANA_POWERUP_RSC = "images/bananapowerup.png";
	public static final String CLOAK_POWERUP_RSC = "images/cloakpowerup.png";
	public static final String GAMEOVER_IMG_RSC = "images/gameover.png";
	public static final String SHIELD_RED_FLAG = "images/redShield.png";
	public static final String SHIELD_BLUE_FLAG = "images/blueShield.png";
	public static final String SHOWCASE_SHIELD = "images/showcaseShield.png";
	public static final String FREEZE_RSC = "images/freeze.png";
	public static final String KEY_RSC = "images/key.png";
	public static final String CASTLE_BLUE_RSC = "images/castle_blue.png";
	public static final String CASTLE_RED_RSC = "images/castle_red.png";
	public static final String FIRE_IMAGE_RSC = "images/fire_image.png";
	public static final String STAR_RSC = "images/star.png";
	
	//sound resources
	public static final String PLAYER_SHOOTSND_RSC = "sounds/gun_shot.wav";	
	public static final String BLUE_PLAYER_MOVING_IMG_RSC = "images/blue_players.png";	
	public static final String RED_PLAYER_MOVING_IMG_RSC = "images/red_players.png";
	
	TEAM myTeam;
	
	// network related values
	String sessionType;  // whether I am just a client or a client with a "server".
	Queue<Message> receivedPackets = new ConcurrentLinkedQueue<Message>();
	// server sends this to all clients
	// A client sends to the server only
	Queue<Message> sendPackets  = new ConcurrentLinkedQueue<Message>();
	ArrayList<ConnectedPlayers> connectedPlayers = new ArrayList<>();
	ConnectedPlayers broadcastServer;
	
	// stupid way to track other client entities
	// stupid way works best sometimes
	HashMap<UUID, Player> clientPlayer = new HashMap<>();
	HashMap<UUID, Bullet> clientBullets = new HashMap<>();
	//HashMap<UUID, CastleFire> clientCastleFires = new HashMap<>();
	HashMap<UUID, Creep> creeps = new HashMap<>();
	HashMap<UUID, HeartPowerUp> heartPowerup = new HashMap<>();
	HashMap<UUID, BananaPowerUp> bananaPowerup = new HashMap<>();
	HashMap<UUID, FreezePowerUp> freezePowerup = new HashMap<>();
	HashMap<UUID, VanishingAct> vanishPowerup = new HashMap<>();
	HashMap<UUID, ShieldFlags> shieldFlagPowerup = new HashMap<>();
	HashMap<UUID, KeyPowerUp> keyPowerup = new HashMap<>();
	HashMap<UUID, StarPowerUp> starPowerup = new HashMap<>();
	HashMap<UUID, Flag> flags = new HashMap<>();
	HashMap<UUID, Castle> castles = new HashMap<>();
	
	public EmpireWars(String title) {
		super(title);
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
	}
	
	
	@Override
	public void initStatesList(GameContainer container) throws SlickException {

		// add game states
		addState(new SplashScreenState());
		addState(new PlayState());
		addState(new SessionState());
		addState(new MenuState());
		addState(new ServerLobbyState());
		addState(new ClientLobbyState());
		addState(new GameOverState());
		
		ResourceManager.loadImage(PLAYER_IMG_RSC);
		ResourceManager.loadImage(PLAYER_MOVINGIMG_RSC);
		ResourceManager.loadImage(SPLASH_SCREEN_IMG_RSC);
		ResourceManager.loadImage(LOGO_IMG_RSC);
		ResourceManager.loadImage(HEART_POWERUP_RSC);
		ResourceManager.loadImage(BANANA_POWERUP_RSC);
		ResourceManager.loadImage(CLOAK_POWERUP_RSC);
		ResourceManager.loadImage(GAMEOVER_IMG_RSC);
		ResourceManager.loadImage(SHIELD_RED_FLAG);
		ResourceManager.loadImage(SHIELD_BLUE_FLAG);
		ResourceManager.loadImage(SHOWCASE_SHIELD);

		ResourceManager.loadImage(PLAYER_BULLETIMG_RSC);
		ResourceManager.loadSound(PLAYER_SHOOTSND_RSC);
		ResourceManager.loadImage(MENU_BUTTONS_RSC);
		ResourceManager.loadImage(BLUE_PLAYER_MOVING_IMG_RSC);
		ResourceManager.loadImage(RED_PLAYER_MOVING_IMG_RSC);
		ResourceManager.loadImage(FLAG_GREYIMG_RSC);
		ResourceManager.loadImage(FLAG_BLUEIMG_RSC);
		ResourceManager.loadImage(FLAG_REDIMG_RSC);
		ResourceManager.loadImage(CREEP_MOVING_IMG_RSC);

		ResourceManager.loadImage(FREEZE_RSC);
		ResourceManager.loadImage(KEY_RSC);
		
		ResourceManager.loadImage(CASTLE_BLUE_RSC);
		ResourceManager.loadImage(CASTLE_RED_RSC);
		ResourceManager.loadImage(FIRE_IMAGE_RSC);
		ResourceManager.loadImage(STAR_RSC);

		map = new TiledMap("src/tilemaps/maze1.tmx");
		mapWidth = map.getWidth() * map.getTileWidth();
		mapHeight = map.getHeight() * map.getTileHeight();
		
		tileHeight = map.getTileHeight();
        tileWidth = map.getTileWidth();
        camera = new Camera(map, mapWidth, mapHeight);
	}
	
	public void createOnClients() {
		player = new Player(tileWidth * 5, tileHeight * 6, 0, 0, this.myTeam);
	}
	
	public int[] randomizeEntityPos() {
		int[] pos = new int[2];
		Random rand = new Random();
        int roadIndex = map.getLayerIndex("road");
        int wallIndex = map.getLayerIndex("walls");
		int xTilePos, yTilePos;
	    	while(true)	{
	    		xTilePos = rand.nextInt(this.mapWidth/tileWidth);
	        	yTilePos = rand.nextInt(this.mapHeight/tileHeight);
	        	if (map.getTileId(xTilePos, yTilePos, roadIndex) != 0 && map.getTileId(xTilePos, yTilePos, wallIndex) == 0) {
	        		if (xTilePos - 1 <= 0 || map.getTileId(xTilePos-1, yTilePos, wallIndex) != 0)
	        			continue;
	        		
	        		if (yTilePos - 1 <= 0 || map.getTileId(xTilePos, yTilePos-1, wallIndex) != 0)
	        			continue;
	        		
	        		if (xTilePos + 1 >= (int)mapWidth/tileWidth || map.getTileId(xTilePos+1, yTilePos, wallIndex) != 0)
	        			continue;
	        		
	        		if (yTilePos + 1 >= (int)mapHeight/tileHeight || map.getTileId(xTilePos, yTilePos+1, wallIndex) != 0)
	        			continue;
	        		
	        		break;
	        	}
	    	}
	    	pos[0] = xTilePos;
	    	pos[1] = yTilePos;
	    	return pos;
	}
	
	
	public void createOnServer() {
		int[] tilePos = new int[2];
		// server is always the red team
		this.myTeam = TEAM.RED;
		
		// heart power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
	    		tilePos = this.randomizeEntityPos();
	    		HeartPowerUp heartTemp = new HeartPowerUp(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
	    		this.heartPowerup.put(heartTemp.getObjectUUID(), heartTemp);
		}
		// shield power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
	    		tilePos = this.randomizeEntityPos();
	    		ShieldFlags shieldemp = new ShieldFlags(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
	    		this.shieldFlagPowerup.put(shieldemp.getObjectUUID(), shieldemp);
		}
		// vanish power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
	    		tilePos = this.randomizeEntityPos();
	    		VanishingAct vanishemp = new VanishingAct(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
	    		this.vanishPowerup.put(vanishemp.getObjectUUID(), vanishemp);
		}
		// star bullet power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
	    		tilePos = this.randomizeEntityPos();
	    		StarPowerUp starTemp = new StarPowerUp(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
	    		this.starPowerup.put(starTemp.getObjectUUID(), starTemp);
		}
		// banana power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
	    		tilePos = this.randomizeEntityPos();
	    		BananaPowerUp bananaTemp = new BananaPowerUp(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
	    		this.bananaPowerup.put(bananaTemp.getObjectUUID(), bananaTemp);
	    }
		// freeze power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
			tilePos = this.randomizeEntityPos();
			FreezePowerUp freezeTemp = new FreezePowerUp(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
			this.freezePowerup.put(freezeTemp.getObjectUUID(), freezeTemp);
	    }		
		
		// key power-up
		for (int i = 0; i < EmpireWars.POWERUP_COUNT; i++) {
			 tilePos = this.randomizeEntityPos();
			 KeyPowerUp keyTemp = new KeyPowerUp(tileWidth * tilePos[0], tileHeight * tilePos[1], this);
			 this.keyPowerup.put(keyTemp.getObjectUUID(), keyTemp);
		}		
		
        // creeps
        for (int i = 0; i < EmpireWars.PLAYER_CREEPS; i++) {
	    		tilePos = this.randomizeEntityPos();
	        	Creep tempCreep = new Creep(tileWidth * tilePos[0], tileHeight * tilePos[1], TEAM.BLUE, this.map);
	        	creeps.put(tempCreep.getObjectUUID(), tempCreep);
        }
        
        // flags
        for (int i = 0; i < 10; i++) {
        		tilePos = this.randomizeEntityPos();
	        	Flag flag = new Flag(tileWidth * tilePos[0], tileHeight * tilePos[1]);
	        	flags.put(flag.getObjectUUID(), flag);	
        }
        
    	Castle tempCastle = new Castle(tileWidth*27, tileHeight*9, TEAM.RED);
    	castles.put(tempCastle.getObjectUUID(), tempCastle);
    	tempCastle = new Castle(mapWidth-26*tileWidth, mapHeight-15*tileHeight, TEAM.BLUE);
    	castles.put(tempCastle.getObjectUUID(), tempCastle);
	}
	
	/**
	 * ClientPlayer getter
	 *
	 */
	public HashMap<UUID, Player> getClientPlayer() {
		return clientPlayer;
	}
	
	
	/**
	 * ClientBullet getter
	 * 
	 */
	public HashMap<UUID, Bullet> getClientBullets() {
		return clientBullets;
	}
	
	/*public HashMap<UUID, CastleFire> getClientCastleFires() {
		return clientCastleFires;
	}*/
	
	/**
	 * HeartPowerup getter
	 * 
	 */
	public HashMap<UUID, HeartPowerUp> getHeartPowerup() {
		return heartPowerup;
	}
	
	/**
	 * BananaPowerup getter
	 * 
	 */
	public HashMap<UUID, BananaPowerUp> getBananaPowerup() {
		return bananaPowerup;
	}
	
	/**
	 * StarPowerup getter
	 * 
	 */
	public HashMap<UUID, StarPowerUp> getStarPowerup() {
		return starPowerup;
	}
	
	/**
	 * FreezePowerup getter
	 * 
	 */
	public HashMap<UUID, FreezePowerUp> getFreezePowerup() {
		return freezePowerup;
	}
	
	/**
	 * KeyPowerup getter
	 * 
	 */
	public HashMap<UUID, KeyPowerUp> getKeyPowerup() {
		return keyPowerup;
	}

	/**
	 * vanishPowerup getter
	 * 
	 */
	public HashMap<UUID, VanishingAct> getVanishPowerup() {
		return vanishPowerup;
	}
	
	/**
	 * shieldPowerup getter
	 * 
	 */
	public HashMap<UUID, ShieldFlags> getShieldPowerup() {
		return shieldFlagPowerup;
	}
	
	/**
	 * BananaPowerup getter
	 * 
	 */
	public HashMap<UUID, Flag> getFlags() {
		return this.flags;
	}
	
	/**
	 * Tile Height getter
	 * @return float. The height of a single tile in the map
	 */
	public float getTileHeight() {
		return this.tileHeight;
	}
	
	/**
	 * Tile Width getter
	 * @return float. The width of a single tile in the map
	 */
	public float getTileWidth() {
		return this.tileWidth;
	}
	
	/*
	 * username getter
	 */
	public String getUsername() {
		return username;
	}

	/*
	 * username setter
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/*
	 * clientType getter
	 */
	public String getSessionType() {
		return sessionType;
	}

	/**
	 * clientType getter
	 * @param clientType. The clientType
	 */
	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}
	
	/*
	 * connected-players getter
	 */
	public ArrayList<ConnectedPlayers> getConnectedPlayers() {
		return this.connectedPlayers;
	}
	
	/*
	 * Adds a new client to the connected player arraylist.
	 */
	public void appendConnectedPlayers(ConnectedPlayers player) {
		// check if player exists first.
		for (Iterator<ConnectedPlayers> i = this.getConnectedPlayers().iterator(); i.hasNext(); ) {
			ConnectedPlayers tempPlayer = i.next();
			if (player.getUsername().equals(tempPlayer.getUsername())) {
				return;
			}
		}
		this.connectedPlayers.add(player);
	}
	
	/*
	 * send packets getter
	 */
	public Queue<Message> getSendPackets() {
		return this.sendPackets;
	}
	
	/*
	 * Adds a new messages in the send queue.
	 */
	public void appendSendPackets(Message msg) {
		this.sendPackets.add(msg);
	}
	
	/*
	 * Removes sent from send queue.
	 */
	public void popSendPackets(Message msg) {
		this.sendPackets.remove(msg);
	}
	
	/*
	 * receivedpackets getter.
	 */
	public Queue<Message> getReceivedPackets() {
		return this.receivedPackets;
	}
	
	/*
	 * Adds a new messages in the received queue.
	 */
	public void appendReceivedPackets(Message msg) {
		this.receivedPackets.add(msg);
	}
	
	/**
	 * broadCastServer getter.
	 * 
	 */
	public ConnectedPlayers getBroadcastServer() {
		return broadcastServer;
	}

	/**
	 * Creates a connectPlayer object that points to where the server is located
	 */
	public void setBroadcastServer(ConnectedPlayers broadcastServer) {
		this.broadcastServer = broadcastServer;
	}
	
	/*
	 * Creates the client and server instances.
	 * This will be on separate threads
	 */
	public void startUpServers(DatagramSocket s) {
		this.socketClient = new GameClient(this, s);
		this.socketServer = new GameServer(this, s);
		socketClient.start();
		socketServer.start();
	};
	
	/*
	 * Stops the servers and clean up
	 */
	public void killServers() {
		if (this.socketClient != null) {
			this.socketClient.killClient();
			socketClient = null;
		}
		if (this.socketServer != null) {
			this.socketServer.killServer();
			socketServer = null;
		}
		sessionType = "";
		clientPlayer = new HashMap<>();
		clientBullets = new HashMap<>();
		receivedPackets = new ConcurrentLinkedQueue<Message>();
		sendPackets  = new ConcurrentLinkedQueue<Message>();
		connectedPlayers = new ArrayList<>();
		broadcastServer = null;
		creeps = new HashMap<>();
		heartPowerup = new HashMap<>();
		keyPowerup = new HashMap<>();
		bananaPowerup = new HashMap<>();
		freezePowerup = new HashMap<>();
		vanishPowerup = new HashMap<>();
		flags = new HashMap<>();
		shieldFlagPowerup = new HashMap<>();
		starPowerup = new HashMap<>();
		castles = new HashMap<>();
	};
	
	/**
	 * creep getter
	 */
	public HashMap<UUID, Creep> getCreeps() {
		return this.creeps;
	}

	/**
	 * castle getter
	 */
	public HashMap<UUID, Castle> getCastle() {
		return this.castles;
	}
	
	/**
	 * Socket client getter
	 * 
	 */
	public GameClient getSocketClient() {
		return this.socketClient;
	}
	/**
	 * Team setter
	 * @param team. The team color to set to.
	 */
 	public void setMyTeam(TEAM team) {
 		this.myTeam = team;
 	}
	/**
	 * lives getter
	 *  
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * lives setter
	 * 
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	/**
	 * free card setter
	 * 
	 */
	public void setFreeCards(int freeCards) {
		this.freeCards = freeCards;
	}
	
	/**
	 * free card getter
	 *  
	 */
	public int getFreeCards() {
		return freeCards;
	}
	
	/**
	 * star bullet setter
	 * 
	 */
	public void setStarBullets(int bullets) {
		this.bullets = bullets;
	}
	
	/**
	 * star bullet getter
	 *  
	 */
	public int getStarBullets() {
		return bullets;
	}
	
	
	

	/**
	 * player getter
	 * 
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	// return the index of the tile that this coordinates belong to
	public static Vector getTileIdx(Vector v){
		return new Vector(v.getX()/32, v.getY()/32);
	}
	
	//return the center of that tile in terms of coordinates
	public static Vector tile2pos(Vector v){
		return new Vector(v.getX()*32+16, v.getY()*32+16);
	}
	
	public static Boolean collidesWithWalls(Vector v, TiledMap map, int collisionLayer)
	{
		int []xy_vals = {-14,+14};
		for (int i=0; i<2;i++)
		{
			for (int j=0; j<2;j++)
			{
				Vector cornerTile = EmpireWars.getTileIdx(new Vector(v.getX()+xy_vals[i], v.getY()+xy_vals[j]));
				if ((int)cornerTile.getX() < 0 || (int)cornerTile.getY() < 0 || (int)cornerTile.getX() > 200 || (int)cornerTile.getY() > 50)
					return true;
					
				if (map.getTileId((int)cornerTile.getX(), (int)cornerTile.getY(), collisionLayer) != 0)
				{
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Score getter
	 */
	public Score getScore() {
		return score;
	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new EmpireWars("Empire Wars"));
			app.setDisplayMode(EmpireWars.SCREEN_SMALL_WIDTH, EmpireWars.SCREEN_SMALL_HEIGHT, false);
			app.setShowFPS(false);
			app.setVSync(true);
			app.setAlwaysRender(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}
