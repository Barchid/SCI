package wator;

import java.util.ArrayList;
import java.util.List;

import core.Agent;
import core.AppConfig;
import core.Environment;
import core.Scheduler;

/**
 * 
 * @author samib
 *
 *         Class that implements the scheduling for a MAS that implements the
 *         bouncing model
 */
public class WatorScheduler extends Scheduler<WatorAppConfig> {
	private List<Agent> newBorns; // the list of agents that are born in the current tour

	public WatorScheduler(WatorAppConfig appConfig, Environment environment) {
		super(appConfig, environment);
		this.newBorns = new ArrayList<Agent>(appConfig.getNbParticles());
	}

	@Override
	protected void makeDistribution(Agent[][] grid) {
		this.createFishes(grid);
		this.createSharks(grid);
	}

	/**
	 * Creates the distribution of fishes for the wator simulation
	 * 
	 * @param grid
	 */
	private void createFishes(Agent[][] grid) {
		for (int i = 0; i < this.appConfig.getNbFishes(); i++) {
			int posX = 0;
			int posY = 0;

			// Find a random (x,y) coordinates that are not used by another agent
			do {
				// position distribution
				posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
				posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
			} while (grid[posX][posY] != null);

			grid[posX][posY] = new Fish(posX, posY, this.environment, this.appConfig, this);
			this.agents.add(grid[posX][posY]);
		}
	}

	@Override
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
			this.purgeCorpses();
			this.mergeNewBorns();
			this.setChanged();
			this.notifyObservers();

			if (this.appConfig.hasTrace()) {
				System.out.println("Tick;" + (i + 1) + ";" + this.countSharks() + ";" + this.countFishes());
				this.printFishAges();
				this.printSharkAges();
			}
			// sleep for delay
			Thread.sleep(this.appConfig.getDelay());
		}
	}

	/**
	 * Keeps the agents list up-to-date by removing the agents that are dead
	 */
	private void purgeCorpses() {
		List<Agent> removed = new ArrayList<Agent>();
		for (Agent agent : this.agents) {
			// IF [agent is dead (not present in environment)]
			if (agent.isDead()) {
				removed.add(agent);
			}
		}

		// remove the agents in the removeIdx list
		this.agents.removeAll(removed);
	}

	/**
	 * Merges the newborns with the current agent list
	 */
	private void mergeNewBorns() {
		for (Agent newBorn : this.newBorns) {
			// IF [newBorn is alive (present in environment)]
			if (this.environment.getCell(newBorn.getPosX(), newBorn.getPosY()) != null) {
				// Add newBorn in agent list
				this.agents.add(newBorn);
			}
		}

		// Clear newBorns list for next turn
		this.newBorns.clear();
	}

	private void createSharks(Agent[][] grid) {
		for (int i = 0; i < this.appConfig.getNbSharks(); i++) {
			int posX = 0;
			int posY = 0;

			// Find a random (x,y) coordinates that are not used by another agent
			do {
				// position distribution
				posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
				posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
			} while (grid[posX][posY] != null);

			grid[posX][posY] = new Shark(posX, posY, this.environment, this.appConfig, this);
			this.agents.add(grid[posX][posY]);
		}
	}

	/**
	 * Adds the new born agent in parameter in the list of newborn agents
	 * 
	 * @param agent
	 */
	public void birth(Agent agent) {
		this.newBorns.add(agent);
		if (this.appConfig.hasTrace()) {
			if (agent instanceof Fish) {
				System.out.println("Agent;Birth;Fish;" + agent.getPosX() + ";" + agent.getPosY());
			} else {
				System.out.println("Agent;Birth;Shark;" + agent.getPosX() + ";" + agent.getPosY());
			}
		}
	}

	/**
	 * Counts the number of sharks in the wa tor simulation
	 * 
	 * @return
	 */
	private int countSharks() {
		int sharks = 0;
		for (Agent agent : this.agents) {
			if (agent instanceof Shark) {
				sharks++;
			}
		}
		return sharks;
	}

	/**
	 * Counts the number of fishes in the wa tor simulation
	 * 
	 * @return
	 */
	private int countFishes() {
		int fishes = 0;
		for (Agent agent : this.agents) {
			if (agent instanceof Fish) {
				fishes++;
			}
		}
		return fishes;
	}

	private void printFishAges() {
		StringBuilder sb = new StringBuilder();
		sb.append("FishAges");
		for (int age = 0; age < 16; age++) {
			int nbFishes = 0;
			for (Agent agent : this.agents) {
				if (agent instanceof Fish && ((Fish) agent).getAge() == age) {
					nbFishes++;
				}
			}
			sb.append(";" + nbFishes);
		}
		System.out.println(sb.toString());
	}
	
	private void printSharkAges() {
		StringBuilder sb = new StringBuilder();
		sb.append("SharkAges");
		for (int age = 0; age < 24; age++) {
			int nbSharks = 0;
			for (Agent agent : this.agents) {
				if (agent instanceof Shark && ((Shark) agent).getAge() == age) {
					nbSharks++;
				}
			}
			sb.append(";" + nbSharks);
		}
		System.out.println(sb.toString());
	}
}
