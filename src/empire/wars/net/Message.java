package empire.wars.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.UUID;

import empire.wars.EmpireWars;

/*
 * We try restrict our game to sending packets using this class.
 * This makes it easier to handle the messaging system since all
 * we expect is data constructed in a certain fashion.
 * 
 * MESSAGE CATEGORY TYPES
 * 
 * SETVEL: Contains velocity updates.
 * SETPOS: Contains position updates.
 * 
 */
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private UUID objectUUID;
	private String msg;
	private String msgType; // e.g UPDATE, CONTROL
	private String categoryType; // more specific e.g SETPOS.
	private String className; // The class that is being updated
	private InetAddress ipAddress; // sender
	private int port = 0; // sender
	private Boolean singleClient = false;

	// mostly used for control packets such as creating connections
	public Message(String msg, String messageType) {
		this.msg = msg;
		this.msgType = messageType;
	}
	
	// Use this for class updates
	public Message(
			UUID objectUUID, String messageType, String categoryType,
			String msg, String className) {
		this.objectUUID = objectUUID;
		this.msg = msg;
		this.msgType = messageType;
		this.categoryType = categoryType;
		this.className = className;
	}
	
	/**
	 * ObjectUUID setter
	 * @param uuid. The new UUID. This allows us to 
	 * change the UUID for newly created objects when need be
	 */
	public void setObjectUUID(UUID uuid) {
		this.objectUUID = uuid;
	}
	
	/*
	 * ObjectUUID getter.
	 */
	public UUID getObjectUUID() {
		return this.objectUUID;
	}
	
	/**
	 *  MSG getter
	 * @return String. THe message string.
	 */
	public String getMsg() {
		return this.msg;
	}
	
	/**
	 * MsgType getter
	 */
	public String getMsgType() {
		return this.msgType;
	}
	
	/*
	 * CategoryType getter
	 */
	public String getCategoryType() {
		return this.categoryType;
	}
	
	/*
	 * className getter.
	 */
	public String getClassName() {
		return this.className;
	}
	
	/**
	 * Determines which handler will process the message
	 * @param msgPacket. The packet sent across the network.
	 * @param ew. The current game state.
	 */
	public static void determineHandler(Message msgPacket, EmpireWars ew) {
		String msgType = msgPacket.getMsgType();
		String className = msgPacket.getClassName();
		
		if (className != null && className.equals("PLAYER")) {
			new PlayerMessageHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("BANANAPOWERUP")) {
			new BananaPowerUpHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("FREEZEPOWERUP")) {
			new FreezePowerUpHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("FLAG")) {
			new FlagMessageHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("HEARTPOWERUP")) {
			new HeartPowerUpHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("KEYPOWERUP")) {
			new KeyPowerUpHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("VANISHINGACT")) {
			new VanishingActHandler(msgPacket, ew);
		}
		
		if(className != null && className.equals("SHIELDFLAGS")) {
			new ShieldFlagsHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("BULLET")) {
			new BulletMessageHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("CASTLE")) {
			new CastleMessageHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("STARPOWERUP")) {
			new StarPowerUpHandler(msgPacket, ew);
		}
		
		if (className != null && className.equals("CREEP")) {
			new CreepMessageHandler(msgPacket, ew);
		}
		
		if (msgType != null && msgType.equals("CONNECT")) {
			new SessionHandler(msgPacket, ew);
		}
		
		if (msgType != null && msgType.equals("START")) {
			new StartGameHandler(msgPacket, ew);
		}
		
		if (msgType != null && msgType.equals("END")) {
			new EndGameHandler(msgPacket, ew);
		}
		
		if (msgType != null && (
				msgType.equals("REDSCORE") || msgType.equals("BLUESCORE") || 
				msgType.equals("SERVERREDSCORE") || msgType.equals("SERVERBLUESCORE"))) {
			new ScoreMessageHandler(msgPacket, ew);
		}
		
	}

	/*
	 * port getter
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Port setter
	 */
	public void setPort(int port) {
		if (this.port == 0) {
			this.port = port;
		}
	}

	/**
	 * ip-address getter 
	 * 
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/*
	 * ip-address setter
	 */
	public void setIpAddress(InetAddress ipAddress) {
		if (this.ipAddress == null) {
			this.ipAddress = ipAddress;
		}
	}
	
	/*
	 * single client getter
	 */
	public Boolean getSingleClient() {
		return singleClient;
	}

	/**
	 * single client setter
	 * @param singleClient
	 */
	public void setSingleClient(Boolean singleClient) {
		this.singleClient = singleClient;
	}
}
