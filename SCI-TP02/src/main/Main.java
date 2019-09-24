package main;

import environment.Environment;
import scheduler.Scheduler;
import utils.AppConfig;
import view.View;

/**
 * 
 * @author samib
 * 
 *         Class that runs the multi agent system
 */
public class Main {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		String propertiesPath = args.length > 0 ? args[0] : "config.properties";
		AppConfig appConfig = new AppConfig(propertiesPath);
		Environment environment = new Environment(appConfig);
		Scheduler scheduler = new Scheduler(appConfig, environment);
		scheduler.initialize();
		View view = new View(environment, appConfig);
		scheduler.addObserver(view);
		scheduler.run();
	}
}
