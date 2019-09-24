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
public class ParticleScheduler extends Scheduler {

	public ParticleScheduler(AppConfig appConfig, Environment environment) {
		super(appConfig, environment);
	}

	@Override
	protected Agent createAgent(int posX, int posY, int pasX, int pasY) {
		return new Particle(posX, posY, pasX, pasY, this.environment);
	}

	@Override
	public void printStats(int nbCollisions) {
		super.printStats(nbCollisions);
		this.agents.forEach((Agent p) -> System.out
				.println("Agent;" + p.getPosX() + ";" + p.getPosY() + ";" + p.getPasX() + ";" + p.getPasY() + ";"));
	}
}
