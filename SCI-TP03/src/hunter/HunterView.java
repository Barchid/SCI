package hunter;

import core.AppConfig;
import core.Environment;
import core.View;

public class HunterView extends View {

	public HunterView(Environment environment, AppConfig appConfig, Avatar avatar) {
		super(environment, appConfig);
		this.simulationPanel.addKeyListener(avatar);
	}
}
