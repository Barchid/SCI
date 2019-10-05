package wator;

import java.awt.Color;

import core.Agent;
import core.Environment;

/**
 * 
 * @author samib
 * 
 *         Only Agent in the bouncing MAS
 */
public class Fish extends Agent {
	private WatorAppConfig appConfig;
	private WatorScheduler scheduler;
	private int breedTime;

	public Fish(int posX, int posY, Environment environment, WatorAppConfig appConfig, WatorScheduler scheduler) {
		super(posX, posY, environment);
		this.color = Color.GREEN;
		this.appConfig = appConfig;
		this.breedTime = this.appConfig.getFishBreedTime();
		this.scheduler = scheduler;
	}

	@Override
	public void decide() {
		// IF [dead] then do nothing
		if (this.isDead) {
			return;
		}

		this.color = Color.GREEN;
		boolean canFuck = this.breedTime == 0;

		int[] coordinates = this.chooseCoordinates();

		// fish cannot move or fuck because there is no place to do so
		if (coordinates == null) {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		this.environment.moveAgent(this, coordinates[0], coordinates[1]);

		// Time to fuck-zer
		if (canFuck) {
			this.fuck(this.getPosX(), this.getPosY());
		}
		// Update breedtime in order to wait the holy fuck time
		else {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
		}
		this.posX = coordinates[0];
		this.posY = coordinates[1];
	}

	/**
	 * Choose coordinates to move based on the random numbers
	 * 
	 * @return the table with coordinates (x,y)
	 */
	private int[] chooseCoordinates() {
		// Choose new (posX,posY) location for the Fish
		int pasX, pasY;

		// at least pasX or pasY not null
		do {
			pasX = this.appConfig.getRandom().nextInt(3) - 1;
			pasY = this.appConfig.getRandom().nextInt(3) - 1;
		} while (pasX == 0 && pasY == 0);

		// choose one free cell based on the random steps (+1 logic)
		for (int x = pasX; x <= 1; x++) {
			for (int y = pasY; y <= 1; y++) {
				int newX = this.posX + x;
				int newY = this.posY + y;

				// not a good choice with isTorus
				if (!this.appConfig.isTorus() && this.environment.isOutOfBound(newX, newY)) {
					continue;
				}

				// change x or y if environment is a torus
				if (this.appConfig.isTorus()) {
					if (newX < 0) {
						newX = this.environment.getWidth() - 1;
					}

					if (newX >= this.environment.getWidth()) {
						newX = 0;
					}

					if (newY < 0) {
						newY = this.environment.getHeight() - 1;
					}

					if (newY >= this.environment.getHeight()) {
						newY = 0;
					}
				}

				// Fish can move here
				if (this.environment.getCell(newX, newY) == null) {
					return new int[] { newX, newY };
				}
			}
		}

		// choose one free cell based on the random steps (-1 logic)
		for (int x = pasX; x >= -1; x--) {
			for (int y = pasY; y >= -1; y--) {
				int newX = this.posX + x;
				int newY = this.posY + y;

				// not a good choice with isTorus
				if (!this.appConfig.isTorus() && this.environment.isOutOfBound(newX, newY)) {
					continue;
				}

				// change x or y if environment is a torus
				if (this.appConfig.isTorus()) {
					if (newX < 0) {
						newX = this.environment.getWidth() - 1;
					}

					if (newX >= this.environment.getWidth()) {
						newX = 0;
					}

					if (newY < 0) {
						newY = this.environment.getHeight() - 1;
					}

					if (newY >= this.environment.getHeight()) {
						newY = 0;
					}
				}

				// Fish can move here
				if (this.environment.getCell(newX, newY) == null) {
					return new int[] { newX, newY };
				}
			}
		}

		// no available cell if we arrive here
		return null;
	}

	/**
	 * Current fish fucks in order to create another fish at the coordinates in
	 * parameters
	 * 
	 * @param x
	 * @param y
	 */
	private void fuck(int x, int y) {
		Fish son = new Fish(x, y, this.environment, this.appConfig, this.scheduler);
		son.color = Color.YELLOW; // yellow when fish is a newborn
		this.environment.addAgent(son, x, y);
		this.scheduler.birth(son);
		this.breedTime = this.appConfig.getFishBreedTime(); // reset breed time
	}
}
