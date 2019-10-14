package wator;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import core.Agent;
import core.AppConfig;
import core.Environment;

public class WatorSimulationPanel extends JPanel {

	private static final long serialVersionUID = -2650318584384164263L;

	protected AppConfig appConfig;
	protected int cellSide;
	protected Environment environment;

	public WatorSimulationPanel(AppConfig appConfig, Environment environment) {
		super();
		this.appConfig = appConfig;
		this.environment = environment;
		this.cellSide = this.appConfig.getBoxSize();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int x = 0; x < this.environment.getWidth(); x++) {
			for (int y = 0; y < this.environment.getHeight(); y++) {
				g.setColor(new Color(46, 134, 222));
				g.fillRect(x * this.cellSide, y * this.cellSide, this.cellSide, this.cellSide);
				if (this.appConfig.isGrid()) {
					g.setColor(Color.BLACK);
					g.drawRect(x * this.cellSide, y * this.cellSide, this.cellSide, this.cellSide);
				}

				Agent particle = this.environment.getCell(x, y);
				if (particle != null) {
					g.setColor(particle.getColor());
					g.fillOval(x * this.cellSide, y * this.cellSide, this.cellSide, this.cellSide);
				}
			}
		}
	}
}
