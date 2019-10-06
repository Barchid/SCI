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
	private int tick;
	private int invincibilityCount;

	public Avatar(int posX, int posY, Environment environment, HunterAppConfig appConfig, Dijkstra dijkstra) {
		super(posX, posY, environment);
		this.color = Color.YELLOW;
		this.appConfig = appConfig;
		this.dijkstra = dijkstra;
		this.tick = 0;
		this.invincibilityCount = 0;
	}

	@Override
	public void decide() {
		this.tick++;
		this.invincibilityCount = this.invincibilityCount > 0 ? this.invincibilityCount - 1 : this.invincibilityCount;
		if (this.invincibilityCount <= 0) {
			this.color = Color.YELLOW;
		} else {
			this.color = Color.ORANGE;
		}

		if (this.tick % this.appConfig.getSpeedAvatar() != 0) {
			return;
		}

		this.tick = 0;
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
		} else if (this.environment.getCell(newX, newY) != null
				&& this.environment.getCell(newX, newY) instanceof Defender) {
			// Eat the defender
			Defender defender = (Defender) this.environment.getCell(newX, newY);
			this.eatDefender(defender);
		} else if (this.environment.getCell(newX, newY) != null
				&& this.environment.getCell(newX, newY) instanceof Winner) {
			// Eat the defender
			Winner winner = (Winner) this.environment.getCell(newX, newY);
			this.eatWinner(winner);
			this.color = Color.WHITE; // tell the scheduler the Avatar has won
		} else if (this.environment.getCell(newX, newY) != null) {
			newX = this.posX;
			newY = this.posY;
		}
		this.environment.moveAgent(this, newX, newY);
		this.posX = newX;
		this.posY = newY;
	}

	private void eatDefender(Defender defender) {
		this.invincibilityCount = this.appConfig.getInvincibilityTime();
		this.environment.removeAgent(defender.getPosX(), defender.getPosY());
		defender.setDead(true);
	}

	private void eatWinner(Winner winner) {
		this.environment.removeAgent(winner.getPosX(), winner.getPosY());
		winner.setDead(true);
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
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public int getInvincibilityCount() {
		return invincibilityCount;
	}
	
	public void setTick(int tick) {
		this.tick = tick;
	}
	
	public void setInvincibilityCount(int invincibilityCount) {
		this.invincibilityCount = invincibilityCount;
	}
}
