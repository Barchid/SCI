package wator;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import core.Agent;
import core.Environment;

/**
 * 
 * @author samib
 * 
 *         Only Agent in the bouncing MAS
 */
public class Shark extends Agent {
	private WatorAppConfig appConfig;
	private int breedTime;
	private int starveTime;
	private WatorScheduler scheduler;

	public Shark(int posX, int posY, Environment environment, WatorAppConfig appConfig, WatorScheduler scheduler) {
		super(posX, posY, environment);
		this.appConfig = appConfig;
		this.breedTime = this.appConfig.getSharkBreedTime();
		this.starveTime = this.appConfig.getSharkStarveTime();
		this.color = Color.RED;
		this.scheduler = scheduler;
	}

	@Override
	public void decide() {
		this.color = Color.RED;
		switch (this.appConfig.getWatorBehavior()) {
		case WatorAppConfig.ONE_ACTION_PER_TICK:
			this.decideOneAction();
			break;
		case WatorAppConfig.FUCK_AND_MOVE:
			this.decideFuckAndMove();
			break;
		case WatorAppConfig.FUCK_AND_EAT:
			this.decideFuckAndEat();
			break;
		default: // FUCK_EAT_AND_MOVE
			this.decideFuckEatAndMove();
			break;
		}
	}

	/**
	 * Decide function when the simulation's behavior is ONE ACTION PER TICK
	 */
	private void decideOneAction() {
		Fish fish = this.findFishNeighbor();
		// IF [shark can eat a fish]
		if (fish != null) {
			this.eat(fish);
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		this.starveTime--; // increase starvation

		// IF [shark starves] THEN dies
		if (this.starveTime == -1) {
			this.die();
			return;
		}

		int[] coordinates = this.chooseCoordinates();

		// IF [shark can't move or fuck]
		if (coordinates == null) {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		// IF [shark can fuck]
		if (this.breedTime == 0) {
			this.fuck(coordinates[0], coordinates[1]);
			return;
		}

		// IF [shark can only move]
		this.environment.moveAgent(this, coordinates[0], coordinates[1]);
		this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
	}

	/**
	 * Decide function when the simulation's behavior is FUCK_AND_MOVE
	 */
	private void decideFuckAndMove() {
		Fish fish = this.findFishNeighbor();
		// IF [shark can eat a fish]
		if (fish != null) {
			this.eat(fish);
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		this.starveTime--; // increase starvation

		// IF [shark starves] THEN dies
		if (this.starveTime == -1) {
			this.die();
			return;
		}

		int[] coordinates = this.chooseCoordinates();

		// IF [shark can't move or fuck]
		if (coordinates == null) {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		// HERE, shark will move and fuck at the old position (if he can)
		this.environment.moveAgent(this, coordinates[0], coordinates[1]);

		// IF [shark can fuck]
		if (this.breedTime == 0) {
			this.fuck(this.getPosX(), this.getPosY());
		}
		// Update breed time if can't fuck
		else {

			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
		}
		// update positions
		this.posX = coordinates[0];
		this.posY = coordinates[1];
	}

	/**
	 * Decide function used when the simulation's behavior is FUCK_AND_EAT
	 */
	private void decideFuckAndEat() {
		Fish fish = this.findFishNeighbor();
		// IF [shark can eat a fish]
		if (fish != null) {
			this.eat(fish);
		} else {
			this.starveTime--; // increase starvation
		}

		// IF [shark starves] THEN dies
		if (this.starveTime == -1) {
			this.die();
			return;
		}

		int[] coordinates = this.chooseCoordinates();

		// IF [shark can't move or fuck]
		if (coordinates == null) {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		// IF [shark can fuck]
		if (this.breedTime == 0) {
			this.fuck(coordinates[0], coordinates[1]);
			return;
		}

		// IF [shark can only move]
		this.environment.moveAgent(this, coordinates[0], coordinates[1]);
		this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
	}

	/**
	 * Decide function with the simulation's behavior is FUCK_EAT_AND_MOVE
	 */
	private void decideFuckEatAndMove() {
		Fish fish = this.findFishNeighbor();
		// IF [shark can eat a fish]
		if (fish != null) {
			this.eat(fish);
			this.environment.moveAgent(this, fish.getPosX(), fish.getPosY());
			if (this.breedTime == 0) {
				this.fuck(this.getPosX(), this.getPosY());
			} else {
				this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			}
			this.posX = fish.getPosX();
			this.posY = fish.getPosY();
			return;
		} else {
			this.starveTime--; // increase starvation
		}

		// IF [shark starves] THEN dies
		if (this.starveTime == -1) {
			this.die();
			return;
		}

		int[] coordinates = this.chooseCoordinates();

		// IF [shark can't move or fuck]
		if (coordinates == null) {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
			return;
		}

		// HERE, shark will move and fuck at the old position (if he can)
		this.environment.moveAgent(this, coordinates[0], coordinates[1]);

		// IF [shark can fuck]
		if (this.breedTime == 0) {
			this.fuck(this.getPosX(), this.getPosY());
		}
		// Update breed time if can't fuck
		else {
			this.breedTime = this.breedTime > 0 ? this.breedTime - 1 : this.breedTime;
		}
		// update positions
		this.posX = coordinates[0];
		this.posY = coordinates[1];
	}

	/**
	 * Current shark eats the specified fish (remove from environment and reset
	 * starvation)
	 * 
	 * @param fish
	 */
	private void eat(Fish fish) {
		fish.setDead(true);
		this.environment.removeAgent(fish.getPosX(), fish.getPosY());
		this.starveTime = this.appConfig.getSharkStarveTime(); // reset starvation

		// Print if required
		if (this.appConfig.hasTrace()) {
			System.out.println("Agent;Death;Fish;" + fish.getPosX() + ";" + fish.getPosY());
		}
	}

	/**
	 * Current shark dies because of starvation
	 */
	private void die() {
		this.environment.removeAgent(this.getPosX(), this.getPosY());
		this.isDead = true;

		// Print if required
		if (this.appConfig.hasTrace()) {
			System.out.println("Agent;Death;Shark;" + this.getPosX() + ";" + this.getPosY());
		}
	}

	/**
	 * Finds a fish that is located in the current shark's neighborhood
	 * 
	 * @return the fish that is located in the current shark's neighborhood or null
	 *         if there is no fish
	 */
	private Fish findFishNeighbor() {
		List<Fish> choices = new ArrayList<Fish>(); // list of choices of coordinates to keep
		for (int pasX = -1; pasX <= 1; pasX++) {
			for (int pasY = -1; pasY <= 1; pasY++) {
				int newX = this.posX + pasX;
				int newY = this.posY + pasY;

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

				Agent agent = this.environment.getCell(newX, newY);
				if (agent != null && agent instanceof Fish) {
					choices.add((Fish) agent);
				}
			}
		}
		if (choices.isEmpty()) {
			return null;
		} else {
			return choices.get(this.appConfig.getRandom().nextInt(choices.size()));
		}
	}

	/**
	 * Choose coordinates to move (free coordinates here) based on the random
	 * numbers. We assume here that there is no fish in the current shark's
	 * neighborhood because the "findFishCoordinates" has been called previously
	 * 
	 * @return the table with coordinates (x,y) or null if the shark cannot move
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
	 * Current shark fucks in order to create another shark at the coordinates in
	 * parameters
	 * 
	 * @param x
	 * @param y
	 */
	private void fuck(int x, int y) {
		Shark son = new Shark(x, y, this.environment, this.appConfig, this.scheduler);
		son.color = Color.MAGENTA; // MAGENTA when shark is a newborn
		this.environment.addAgent(son, x, y);
		this.scheduler.birth(son);
		this.breedTime = this.appConfig.getSharkBreedTime(); // reset breed time
	}
}
