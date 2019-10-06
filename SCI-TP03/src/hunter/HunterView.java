package hunter;

import core.AppConfig;
import core.Environment;
import core.View;

public class HunterView extends View {

	public HunterView(Environment environment, AppConfig appConfig, Avatar avatar, HunterScheduler scheduler) {
		super(environment, appConfig);
		this.simulationPanel.addKeyListener(avatar);
		this.simulationPanel.addKeyListener(scheduler);
	}
}
