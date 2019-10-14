package particles;

import core.AppConfig;
import core.Environment;
import core.Scheduler;
import core.View;

/**
 * 
 * @author samib
 * 
 *         Class that runs the multi agent system
 */
public class Main {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		String propertiesPath = args.length > 0 ? args[0] : "particles.properties";
		AppConfig appConfig = new AppConfig(propertiesPath);
		Environment environment = new Environment(appConfig);
		Scheduler scheduler = new ParticleScheduler(appConfig, environment);
		scheduler.initialize();
		ParticleSimulationPanel simulationPanel = new ParticleSimulationPanel(appConfig, environment);
		View view = new View(environment, appConfig, simulationPanel);
		scheduler.addObserver(view);
		scheduler.run();
	}
}
