package particles;

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
public class ParticleScheduler extends Scheduler<AppConfig> {

	public ParticleScheduler(AppConfig appConfig, Environment environment) {
		super(appConfig, environment);
	}

	@Override
	public void printStats(int nbCollisions) {
		super.printStats(nbCollisions);
		this.agents.forEach((Agent p) -> System.out.println("Agent;" + p.getPosX() + ";" + p.getPosY() + ";"
				+ ((Particle) p).getPasX() + ";" + ((Particle) p).getPasY() + ";"));
	}
	
	@Override
	protected void makeDistribution(Agent[][] grid) {
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

			grid[posX][posY] = new Particle(posX, posY, pasX, pasY, this.environment);
			this.agents.add(grid[posX][posY]);
		}
	}
}
