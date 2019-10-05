package hunter;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import core.Agent;
import core.Environment;

public class Avatar extends Agent implements KeyListener {
	private int dirX;
	private int dirY;
	private HunterAppConfig appConfig;
	private Dijkstra dijkstra;

	public Avatar(int posX, int posY, Environment environment, HunterAppConfig appConfig, Dijkstra dijkstra) {
		super(posX, posY, environment);
		this.color = Color.YELLOW;
		this.appConfig = appConfig;
		this.dijkstra = dijkstra;
	}

	@Override
	public void decide() {
		this.move();
		this.dijkstra.breadthFirstSearch(this.posX, this.posY);
	}

	private void move() {
		int newX = this.posX + dirX;
		int newY = this.posY + dirY;

		if (this.environment.isOutOfBound(newX, newY) && this.appConfig.isTorus()) {
			if (newX < 0) {
				newX = this.environment.getWidth() - 1;
			}

			if (newX >= this.environment.getWidth()) {
				newX = 0;
			}

			if (newY < 0) {
				newY = this.environment.getHeight() - 1;
			}

			if (newY >= this.environment.getHeight()) {
				newY = 0;
			}
		} else if (this.environment.isOutOfBound(newX, newY) && !this.appConfig.isTorus()) {
			newX = this.posX;
			newY = this.posY;
		} else if (this.environment.getCell(newX, newY) != null) {
			newX = this.posX;
			newY = this.posY;
		} else {
			this.environment.moveAgent(this, newX, newY);
			this.posX = newX;
			this.posY = newY;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_Z:
			this.dirY = -1;
			this.dirX = 0;
			break;
		case KeyEvent.VK_Q:
			this.dirX = -1;
			this.dirY = 0;
			break;
		case KeyEvent.VK_S:
			this.dirY = 1;
			this.dirX = 0;
			break;
		case KeyEvent.VK_D:
			this.dirX = 1;
			this.dirY = 0;
			break;
		default:
			System.out.println("pute");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
