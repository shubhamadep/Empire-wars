package empire.wars.net;

import java.net.InetAddress;

import empire.wars.EmpireWars.TEAM;

public class ConnectedPlayers {
	
	private InetAddress ipAddress;
	private int port;
	private String username;
	private TEAM team;
	
	public ConnectedPlayers(String username, InetAddress ipAddress, int port, TEAM team) {
		this.setUsername(username);
		this.setIpAddress(ipAddress);
		this.setPort(port);
		this.setTeam(team);
	}

	/*
	 * ipAddress getter
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/*
	 * ipAddress setter
	 */
	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * port getter
	 */
	public int getPort() {
		return port;
	}

	/*
	 * port setter
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/*
	 * Username getter
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
	
	/**
	 * 
	 * team getter
	 */
	public TEAM getTeam() {
		return team;
	}

	/**
	 * team setter
	 * @param team
	 */
	public void setTeam(TEAM team) {
		this.team = team;
	}

}
