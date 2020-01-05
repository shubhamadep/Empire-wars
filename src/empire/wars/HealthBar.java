package empire.wars;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import empire.wars.EmpireWars.TEAM;

/**
 * Implements the player health.
 * @author peculiaryak
 *
 */
public class HealthBar extends NetworkEntity {
	public final static double MAXIMUM_HEALTH = 16;
	private double currentHealth;
	private TEAM team;
	private double _currentHealth;
	
	public HealthBar(float x, float y, TEAM team) {
		super(x, y);
		this.currentHealth = HealthBar.MAXIMUM_HEALTH;
		this._currentHealth = HealthBar.MAXIMUM_HEALTH;
		this.team = team;
	}
	
	/**
	 * current health getter 
	 * @return
	 */
	public double getCurrentHealth() {
		return this.currentHealth;
	}
	/**
	 * Tracks if the last value sent over the network
	 * for the currentHealth matches what it is currently
	 * @return. true if the values don't match false otherwise 
	 */
	public boolean hasChanged() {
		if (this.currentHealth != this._currentHealth) {
			this._currentHealth = this.currentHealth;
			return true;
		}
		return false;
	}
	/*
	 * current health setter
	 */
	public void setCurrentHealth(double x) {
		this.currentHealth = x;
	}
	
	/*
	 * team setter.
	 */
	public void setTeam(TEAM team) {
		this.team = team;
	}
	/**
	 * Determine if the player is dead.
	 * A player is dead when their health bar gets to zero
	 * @return true if player is dead otherwise false.
	 */
	public boolean isDead() {
		if (this.currentHealth <= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Use to set the health of the player.
	 * Use negative x to reduce the health and 
	 * positive x to increase the value. This can be used
	 * by powerups that increase the players health.
	 * 
	 * @param x. The value to increment or decrement the health bar with
	 */
	public void setHealth(double x) {
		this.currentHealth += x;
		
		if (this.currentHealth > HealthBar.MAXIMUM_HEALTH) {
			// dont go beyond max
			this.currentHealth = HealthBar.MAXIMUM_HEALTH;
		} else if (this.currentHealth <= 0.0) {
			// dont go below min
			this.currentHealth = 0.0;
			this.explode();
		}
	}
	
	/**
	 * Determines how to render the health bar.
	 * The "empty" slots will be black.
	 */
	public void render(final Graphics g) {
		float drawXAt = this.getX();
		float drawYAt = this.getY();
		for (int i = 0; i <  HealthBar.MAXIMUM_HEALTH; i++) {
			if (i < this.currentHealth) {
				if (this.team == TEAM.RED)
					g.setColor(Color.red);
				else
					g.setColor(Color.blue);
				g.fillRect(drawXAt, drawYAt, 2, 3);
			} else {
				g.setColor(Color.black);
				g.fillRect(drawXAt, drawYAt, 2, 3);
			}
			drawXAt += 2;
		}
	}
	
}
