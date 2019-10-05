package hunter;

import core.Agent;
import core.AppConfig;
import core.Environment;
import core.Scheduler;

public class HunterScheduler extends Scheduler<HunterAppConfig, Environment> {
	private Dijkstra dijkstra;
	
	public HunterScheduler(HunterAppConfig appConfig, Environment environment) {
		super(appConfig, environment);
		this.dijkstra = new Dijkstra(this.environment);
	}

	@Override
	protected void makeDistribution(Agent[][] grid) {
		this.createMaze(grid);
		this.createAvatar(grid);
		this.createHunter(grid);
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
			this.setChanged();
			this.notifyObservers();

			if (this.appConfig.hasTrace()) {

			}
			// sleep for delay
			Thread.sleep(this.appConfig.getDelay());
		}
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

	}

	private void createAvatar(Agent[][] grid) {
		int[] freePlace = this.findFreePlace(grid);
		Avatar avatar = new Avatar(freePlace[0], freePlace[1], this.environment, this.appConfig, this.dijkstra);
		grid[freePlace[0]][freePlace[1]] = avatar;
		this.agents.add(avatar);
	}

	private void createHunter(Agent[][] grid) {
		int[] coord = this.findFreePlace(grid);
		Hunter hunter = new Hunter(coord[0], coord[1], this.environment, this.dijkstra);
		grid[coord[0]][coord[1]] = hunter;
		this.agents.add(hunter);
	}

	private int[] findFreePlace(Agent[][] grid) {
		int posX, posY;
		do {
			posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
			posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
		} while (grid[posX][posY] != null);

		return new int[] { posX, posY };
	}
}
