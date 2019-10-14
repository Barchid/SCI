package wator;

import core.AppConfig;

public class WatorAppConfig extends AppConfig {
	private int sharkBreedTime;
	private int sharkStarveTime;
	private int fishBreedTime;
	private int nbSharks;
	private int nbFishes;
	private int watorBehavior; // behavior in general for all the fishes/sharks
	
	// values available for watorbehavior
	public static final int ONE_ACTION_PER_TICK = 0;
	public static final int REPRODUCE_AND_MOVE = 1;
	public static final int REPRODUCE_AND_EAT = 2;
	public static final int REPRODUCE_EAT_AND_MOVE = 3;
	
	public WatorAppConfig(String propertiesPath) throws Exception {
		super(propertiesPath);
		this.sharkBreedTime = Integer.parseInt(this.properties.getProperty("sharkBreedTime"));
		this.sharkStarveTime = Integer.parseInt(this.properties.getProperty("sharkStarveTime"));
		this.fishBreedTime = Integer.parseInt(this.properties.getProperty("fishBreedTime"));
		this.nbSharks= Integer.parseInt(this.properties.getProperty("nbSharks"));
		this.nbFishes = Integer.parseInt(this.properties.getProperty("nbFishes"));
		this.watorBehavior = Integer.parseInt(this.properties.getProperty("watorBehavior"));
	}

	public int getSharkBreedTime() {
		return sharkBreedTime;
	}

	public void setSharkBreedTime(int sharkBreedTime) {
		this.sharkBreedTime = sharkBreedTime;
	}

	public int getSharkStarveTime() {
		return sharkStarveTime;
	}

	public void setSharkStarveTime(int sharkStarveTime) {
		this.sharkStarveTime = sharkStarveTime;
	}

	public int getFishBreedTime() {
		return fishBreedTime;
	}

	public void setFishBreedTime(int fishBreedTime) {
		this.fishBreedTime = fishBreedTime;
	}

	public int getNbSharks() {
		return nbSharks;
	}

	public void setNbSharks(int nbSharks) {
		this.nbSharks = nbSharks;
	}

	public int getNbFishes() {
		return nbFishes;
	}

	public void setNbFishes(int nbFishes) {
		this.nbFishes = nbFishes;
	}

	public int getWatorBehavior() {
		return watorBehavior;
	}

	public void setWatorBehavior(int watorBehavior) {
		this.watorBehavior = watorBehavior;
	}
}
