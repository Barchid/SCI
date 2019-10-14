package core;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * @author samib
 *
 *         Class that keeps the application's UI up to date after an update from
 *         the Scheduler (due to Observer pattern)
 */
@SuppressWarnings("deprecation")
public class View implements Observer {
	protected JPanel panel;
	protected AppConfig appConfig;

	public View(Environment environment, AppConfig appConfig, JPanel panel) {
		super();
		this.appConfig = appConfig;
		this.panel = panel;
		int width = this.appConfig.getBoxSize() * this.appConfig.getGridSizeX();
		int height = this.appConfig.getBoxSize() * this.appConfig.getGridSizeY();
		this.panel.setPreferredSize(new Dimension(width, height));
		this.panel.setFocusable(true);

		JFrame frame = new JFrame("Multi-agent system");
		frame.add(this.panel);
		frame.pack();
		frame.setLocationRelativeTo(this.panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	@Override
	public void update(Observable o, Object arg) {
		this.panel.repaint();
	}
}
