package ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import agent.Particle;
import environment.Environment;
import utils.AppConfig;

/**
 * 
 * @author samib
 *
 *         Class that represents the grid that displays the state of the current
 *         simulation in the Multi agent system
 */
public class SimulationPanel extends JPanel {
	private static final long serialVersionUID = 620784715181712323L;
	private AppConfig appConfig;
	private int cellSide;
	private Environment environment;

	public SimulationPanel(AppConfig appConfig, Environment environment) {
		super();
		this.appConfig = appConfig;
		this.environment = environment;
		this.cellSide = this.appConfig.getBoxSize();
//		JScrollPane sp = new JScrollPane();
//		sp.setOpaque(false);
//		sp.getViewport().setOpaque(false);
//		sp.setViewportView(top);
//		this.add(sp);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int x = 0; x < this.environment.getWidth(); x++) {
			for (int y = 0; y < this.environment.getHeigt(); y++) {
				if (this.appConfig.isGrid()) {
					g.setColor(Color.BLACK);
					g.drawRect(x * this.cellSide, y * this.cellSide, this.cellSide, this.cellSide);
				}

				Particle particle = this.environment.getCell(x, y);
				if (particle != null) {
					g.setColor(particle.getColor());
					g.fillRect(x * this.cellSide, y * this.cellSide, this.cellSide, this.cellSide);

					g.setColor(Color.BLUE);
					if (particle.getPasX() == -1) {
					}

					if (particle.getPasX() == 1) {

					}

					if (particle.getPasY() == -1) {

					}

					if (particle.getPasY() == 1) {

					}
				}
			}
		}
	}

}
