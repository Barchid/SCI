package hunter;

import core.Environment;

public class Main {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		String propertiesPath = args.length > 0 ? args[0] : "hunter.properties";
		HunterAppConfig appConfig = new HunterAppConfig(propertiesPath);
		Environment environment = new Environment(appConfig);
		HunterScheduler scheduler = new HunterScheduler(appConfig, environment);
		scheduler.initialize();
		HunterSimulationPanel panel = new HunterSimulationPanel(appConfig, environment);
		HunterView view = new HunterView(environment, appConfig, scheduler.getAvatar(), scheduler, panel);
		scheduler.addObserver(view);
		scheduler.run();
	}
}
