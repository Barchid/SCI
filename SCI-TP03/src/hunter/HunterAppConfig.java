package hunter;

import core.AppConfig;

public class HunterAppConfig extends AppConfig {
	private int wallsPercent;
	private int nbHunters;
	private int speedHunter;
	private int speedAvatar;
	private int defendersLife;
	private int invincibilityTime;
	private int nbDefenders;

	public HunterAppConfig(String propertiesPath) throws Exception {
		super(propertiesPath);
		this.wallsPercent = Integer.parseInt(this.properties.getProperty("wallsPercent"));
		this.nbHunters = Integer.parseInt(this.properties.getProperty("nbHunters"));
		this.speedHunter = Integer.parseInt(this.properties.getProperty("speedHunter"));
		this.speedAvatar = Integer.parseInt(this.properties.getProperty("speedAvatar"));
		this.defendersLife = Integer.parseInt(this.properties.getProperty("defendersLife"));
		this.invincibilityTime = Integer.parseInt(this.properties.getProperty("invincibilityTime"));
		this.nbDefenders = Integer.parseInt(this.properties.getProperty("nbDefenders"));
	}

	public int getWallsPercent() {
		return wallsPercent;
	}

	public int getNbHunters() {
		return nbHunters;
	}

	public int getSpeedHunter() {
		return speedHunter;
	}

	public int getSpeedAvatar() {
		return speedAvatar;
	}

	public int getDefendersLife() {
		return defendersLife;
	}

	public int getInvincibilityTime() {
		return invincibilityTime;
	}

	public int getNbDefenders() {
		return nbDefenders;
	}

	public void setSpeedHunter(int speedHunter) {
		this.speedHunter = speedHunter;
	}

	public void setSpeedAvatar(int speedAvatar) {
		this.speedAvatar = speedAvatar;
	}
}
