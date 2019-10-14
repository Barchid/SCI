package hunter;

import core.AppConfig;
import core.Environment;
import core.View;

public class HunterView extends View {

	public HunterView(Environment environment, AppConfig appConfig, Avatar avatar, HunterScheduler scheduler, HunterSimulationPanel panel) {
		super(environment, appConfig, panel);
		this.panel.addKeyListener(avatar);
		this.panel.addKeyListener(scheduler);
	}
}
