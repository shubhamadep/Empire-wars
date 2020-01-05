package empire.wars;

import org.newdawn.slick.state.StateBasedGame;

import empire.wars.EmpireWars.TEAM;
import empire.wars.net.Message;

/*
 * Tracks the scores each team gets
 */
public class Score {
	private int redTeam;
	private int blueTeam;
	
	// networking
	private int _redTeam;
	private int _blueTeam;
	
	public Score() {
		this.setRedTeam(0);
		this.setBlueTeam(0);
	}

	public int getRedTeam() {
		return redTeam;
	}

	public void setRedTeam(int redTeam) {
		this.redTeam = redTeam;
	}
	
	/**
	 * Adds point to the redTeam
	 * @param addTeam. The number of points to add
	 */
	public void addRedTeam(int redTeam) {
		this.redTeam += redTeam;
	}

	public int getBlueTeam() {
		return blueTeam;
	}

	public void setBlueTeam(int blueTeam) {
		this.blueTeam = blueTeam;
	}
	
	/**
	 * Adds point to the blueTeam
	 * @param blueTeam. The number of points to add
	 */
	public void addBlueTeam(int blueTeam) {
		this.blueTeam += blueTeam;
	}
	
	/*
	 * Determines which team to add the score to.
	 * @param score. The score to add
	 * @param team. The team to add the score to
	 */
	public void addScore(int score, TEAM team) {
		if (team == TEAM.BLUE) {
			this.addBlueTeam(score);
		} else if (team == TEAM.RED) {
			this.addRedTeam(score);
		}
	}
	
	public void serverSendScoreUpdate(EmpireWars game, int score, TEAM team) {
		String msg = Integer.toString(score);
		String type = "SERVER" + team.toString() + "SCORE";
		Message posUpdate = new Message(msg, type);
		game.sendPackets.add(posUpdate);
	}
	
	/**
	 * This red team propagates the scores to other clients
	 */
	private void sendRedScoreUpdate(EmpireWars ew) {
		if (this.redTeam != this._redTeam) {
			String msg = Integer.toString(this.redTeam);
			Message scoreUpdate = new Message(msg, "REDSCORE"); 
			ew.sendPackets.add(scoreUpdate);
			this._redTeam = this.redTeam;
		}
	}
	
	
	/**
	 * This blue team propagates the scores to other clients
	 */
	private void sendBlueScoreUpdate(EmpireWars ew) {
		if (this.blueTeam != this._blueTeam) {
			String msg = Integer.toString(this.blueTeam);
			Message scoreUpdate = new Message(msg, "BLUESCORE"); 
			ew.sendPackets.add(scoreUpdate);
			this._blueTeam = this.blueTeam;
		}
	}
	
	public void update(StateBasedGame game) {
		EmpireWars ew = (EmpireWars)game;
		this.sendBlueScoreUpdate(ew);
		this.sendRedScoreUpdate(ew);
	}
}
