package view;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

import environment.Environment;
import ui.SimulationPanel;
import utils.AppConfig;

/**
 * 
 * @author samib
 *
 *         Class that keeps the application's UI up to date after an update from
 *         the Scheduler (due to Observer pattern)
 */
@SuppressWarnings("deprecation")
public class View implements Observer {
	private SimulationPanel simulationPanel;
	private AppConfig appConfig;

	public View(Environment environment, AppConfig appConfig) {
		super();
		this.appConfig = appConfig;
		// initialize panel of simulation
		this.simulationPanel = new SimulationPanel(appConfig, environment);
		int width = this.appConfig.getBoxSize() * this.appConfig.getGridSizeX();
		int height = this.appConfig.getBoxSize() * this.appConfig.getGridSizeY();
		this.simulationPanel.setPreferredSize(new Dimension(width, height));
		this.simulationPanel.setFocusable(true);
		
		JFrame frame = new JFrame("Multi-agent system");
		frame.add(this.simulationPanel);
		frame.pack();
		frame.setLocationRelativeTo(simulationPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.simulationPanel.repaint();
	}

}
