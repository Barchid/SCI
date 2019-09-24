package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

/**
 * 
 * @author samib
 *
 *         Class used to run the simulation of the multi agent system and
 *         organize the agents' actions
 */
@SuppressWarnings("deprecation")
public abstract class Scheduler extends Observable {
	protected AppConfig appConfig;
	protected Environment environment;
	protected List<Agent> agents;

	public Scheduler(AppConfig appConfig, Environment environment) {
		super();
		this.appConfig = appConfig;
		this.environment = environment;
		this.agents = new ArrayList<Agent>(this.appConfig.getNbParticles());
	}

	/**
	 * Runs the simulation of the multi agent system
	 */
	public void run() throws Exception {
		this.setChanged();
		this.notifyObservers();
		// sleep for delay
		Thread.sleep(this.appConfig.getDelay());
		for (int i = 0; this.appConfig.getNbTicks() == 0 || i < this.appConfig.getNbTicks(); i++) {
			int nbCollisions = 0;
			// Depending on the selected scheduling
			switch (this.appConfig.getScheduling()) {
			case AppConfig.SCHED_FAIR:
				nbCollisions = this.fairTurn();
				break;
			case AppConfig.SCHED_RAND:
				nbCollisions = this.randomTurn();
				break;
			case AppConfig.SCHED_SEQ:
				nbCollisions = this.sequentialTurn();
				break;
			default:
				return;
			}
			this.setChanged();
			this.notifyObservers();

			if (this.appConfig.hasTrace()) {
				this.printStats(nbCollisions);
			}
			// sleep for delay
			Thread.sleep(this.appConfig.getDelay());
		}
	}

	/**
	 * Prints the stats to get a trace
	 * @param nbCollisions
	 */
	public void printStats(int nbCollisions) {
		System.out.println("Tick;" + nbCollisions);
	}

	/**
	 * Runs one turn of simulation with fair scheduling
	 * 
	 * @return the number of collisions that happened in the turn
	 */
	private int fairTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.RED) {
				nbCollisions++;
			}
		}

		Collections.shuffle(this.agents, this.appConfig.getRandom());
		return nbCollisions;
	}

	/**
	 * Runs one turn of simulation with random scheduling
	 * 
	 * @return the number of collisions that occurs in the turn
	 */
	private int randomTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.RED) {
				nbCollisions++;
			}
		}
		return nbCollisions;
	}

	/**
	 * Runs one turn of simulation with sequential scheduling
	 * 
	 * @return the number of collisions that occurs in the turn
	 */
	private int sequentialTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.RED) {
				nbCollisions++;
			}
		}

		return nbCollisions;
	}

	/**
	 * Initializes the agents in the environment according to the distribution in
	 * AppConfig's seed
	 */
	public void initialize() {
		Agent[][] grid = new Agent[this.appConfig.getGridSizeX()][this.appConfig.getGridSizeY()];

		this.makeDistribution(grid);
		this.environment.setGrid(grid);
	}

	/**
	 * Generates all the particles and place them in the grid
	 */
	private void makeDistribution(Agent[][] grid) {
		for (int i = 0; i < this.appConfig.getNbParticles(); i++) {
			int posX = 0;
			int posY = 0;
			int pasX = 0;
			int pasY = 0;

			// Chose a step of progression to make the particle move (at least, one value
			// has to be non nul
			while (pasX == 0 && pasY == 0) {
				pasX = this.appConfig.getRandom().nextInt(3) - 1; // number between -1 and 1
				pasY = this.appConfig.getRandom().nextInt(3) - 1;
			}

			// Find a random (x,y) coordinates that are not used by another particle
			do {
				// position distribution
				posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
				posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
			} while (grid[posX][posY] != null);

			grid[posX][posY] = this.createAgent(posX, posY, pasX, pasY);
			this.agents.add(grid[posX][posY]);
		}
	}
	
	protected abstract Agent createAgent(int posX, int posY, int pasX, int pasY);
}
