package hunter;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.Agent;
import core.AppConfig;
import core.Environment;
import core.Scheduler;

public class HunterScheduler extends Scheduler<HunterAppConfig, Environment> implements KeyListener {
	private Dijkstra dijkstra;
	private int defenderCount;
	private boolean isReloading;

	public HunterScheduler(HunterAppConfig appConfig, Environment environment) {
		super(appConfig, environment);
		this.dijkstra = new Dijkstra(this.environment);
		this.defenderCount = 0;
		this.isReloading = false;
	}

	@Override
	protected void makeDistribution(Agent[][] grid) {
		this.defenderCount = 0;
		this.createMaze(grid);
		this.createAvatar(grid);
		this.createHunters(grid);
		this.createDefenders(grid);
	}

	@SuppressWarnings("deprecation")
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
			this.activateWinner();

			this.setChanged();
			this.notifyObservers();

			if (this.appConfig.hasTrace()) {

			}
			// sleep for delay
			Thread.sleep(this.appConfig.getDelay());

			if (nbCollisions == -1) {
				break;
			}
		}
	}

	/**
	 * Runs one turn of simulation with fair scheduling
	 * 
	 * @return the number of collisions that happened in the turn
	 */
	protected int fairTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.WHITE) {
				return -1;
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
	protected int randomTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.WHITE) {
				return -1;
			}
		}
		return nbCollisions;
	}

	/**
	 * Runs one turn of simulation with sequential scheduling
	 * 
	 * @return the number of collisions that occurs in the turn
	 */
	protected int sequentialTurn() {
		int nbCollisions = 0;
		for (Agent particle : this.agents) {
			particle.decide();
			if (particle.getColor() == Color.WHITE) {
				return -1;
			}
		}

		return nbCollisions;
	}

	public Avatar getAvatar() {
		return (Avatar) this.agents.stream().filter((Agent agent) -> agent instanceof Avatar).findAny().orElse(null);
	}

	/**
	 * Creates a perfect maze
	 * 
	 * @param grid
	 */
	private void createMaze(Agent[][] grid) {
		int wallCount = (this.appConfig.getGridSizeX() * this.appConfig.getGridSizeY())
				/ this.appConfig.getWallsPercent();

		for (int i = 0; i < wallCount; i++) {
			int[] coord = this.findFreePlace(grid);
			Wall wall = new Wall(coord[0], coord[1], this.environment);
			grid[coord[0]][coord[1]] = wall;
			this.agents.add(wall);
		}
	}

	private void createAvatar(Agent[][] grid) {
		int[] freePlace = this.findFreePlace(grid);
		Avatar avatar = new Avatar(freePlace[0], freePlace[1], this.environment, this.appConfig, this.dijkstra);
		grid[freePlace[0]][freePlace[1]] = avatar;
		this.agents.add(avatar);
	}

	private void createHunters(Agent[][] grid) {
		for (int i = 0; i < this.appConfig.getNbHunters(); i++) {
			int[] coord = this.findFreePlace(grid);
			Hunter hunter = new Hunter(coord[0], coord[1], this.environment, this.dijkstra, this.appConfig,
					this.getAvatar());
			grid[coord[0]][coord[1]] = hunter;
			this.agents.add(hunter);
		}
	}

	private void createDefenders(Agent[][] grid) {
		for (int i = 0; i < this.appConfig.getNbDefenders(); i++) {
			int[] coord = this.findFreePlace(grid);
			Defender defender = new Defender(coord[0], coord[1], this.environment, this.appConfig, this.dijkstra,
					this.getAvatar());
			grid[coord[0]][coord[1]] = defender;
			this.agents.add(defender);
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
				if (agent instanceof Defender) {
					this.defenderCount++;
				}
			}
		}

		// remove the agents in the removeIdx list
		this.agents.removeAll(removed);
	}

	private void activateWinner() {
		if (this.defenderCount < this.appConfig.getNbDefenders()) {
			return;
		}

		// find free place
		int posX, posY;
		do {
			posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
			posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
		} while (this.environment.getCell(posX, posY) != null);

		// Add winner
		Winner winner = new Winner(posX, posY, this.environment);
		this.agents.add(winner);
		this.environment.addAgent(winner, posX, posY);
		this.defenderCount = -1; // disable activeWinner
	}

	private int[] findFreePlace(Agent[][] grid) {
		int posX, posY;
		do {
			posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
			posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
		} while (grid[posX][posY] != null);

		return new int[] { posX, posY };
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int newSpeed;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:

			break;
		case KeyEvent.VK_O: // accelerate hunter
			newSpeed = this.appConfig.getSpeedHunter() == 1 ? this.appConfig.getSpeedHunter()
					: this.appConfig.getSpeedHunter() - 1;
			this.appConfig.setSpeedHunter(newSpeed);
			break;
		case KeyEvent.VK_P: // decelerate hunter
			newSpeed = this.appConfig.getSpeedHunter() + 1;
			this.appConfig.setSpeedHunter(newSpeed);
			break;
		case KeyEvent.VK_L: // accelerate avatar
			newSpeed = this.appConfig.getSpeedAvatar() == 1 ? this.appConfig.getSpeedAvatar()
					: this.appConfig.getSpeedAvatar() - 1;
			this.appConfig.setSpeedAvatar(newSpeed);
			break;
		case KeyEvent.VK_M: // decelerate avatar
			newSpeed = this.appConfig.getSpeedAvatar() + 1;
			this.appConfig.setSpeedAvatar(newSpeed);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
