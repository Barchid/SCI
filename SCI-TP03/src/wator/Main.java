package wator;

import core.Environment;
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
		String propertiesPath = args.length > 0 ? args[0] : "wator.properties";
		WatorAppConfig appConfig = new WatorAppConfig(propertiesPath);
		Environment environment = new Environment(appConfig);
		WatorScheduler scheduler = new WatorScheduler(appConfig, environment);
		scheduler.initialize();
		View view = new View(environment, appConfig);
		scheduler.addObserver(view);
		scheduler.run();
	}
}
