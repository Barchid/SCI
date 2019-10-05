package hunter;

import core.AppConfig;

public class HunterAppConfig extends AppConfig {
	private int wallsPercent;
	private int nbHunters;
	private double speedHunter;
	private double speedAvatar;
	private int defendersLife;

	public HunterAppConfig(String propertiesPath) throws Exception {
		super(propertiesPath);
		this.wallsPercent = Integer.parseInt(this.properties.getProperty("wallsPercent"));
		this.nbHunters = Integer.parseInt(this.properties.getProperty("nbHunters"));
		this.speedHunter = Double.parseDouble(this.properties.getProperty("speedHunter"));
		this.speedAvatar = Double.parseDouble(this.properties.getProperty("speedAvatar"));
		this.defendersLife = Integer.parseInt(this.properties.getProperty("defendersLife"));
	}

	public int getWallsPercent() {
		return wallsPercent;
	}

	public int getNbHunters() {
		return nbHunters;
	}

	public double getSpeedHunter() {
		return speedHunter;
	}

	public double getSpeedAvatar() {
		return speedAvatar;
	}

	public int getDefendersLife() {
		return defendersLife;
	}
}
