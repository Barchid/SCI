package hunter;

import java.awt.Color;
import java.awt.Point;

import core.Agent;
import core.Environment;

public class Hunter extends Agent {
	private Dijkstra dijkstra;
	private int speed;
	private int tick;
	private Avatar avatar;

	public Hunter(int posX, int posY, Environment environment, Dijkstra dijkstra, HunterAppConfig appConfig,
			Avatar avatar) {
		super(posX, posY, environment);
		this.dijkstra = dijkstra;
		this.color = Color.RED;
		this.speed = appConfig.getSpeedHunter();
		this.tick = 0;
		this.avatar = avatar;
	}

	@Override
	public void decide() {
		this.tick++;
		if (this.tick % this.speed != 0) {
			return;
		}

		this.tick = 0;
		if (this.isNextToAvatar()) {
			this.color = Color.WHITE;
			return;
		}

		Point destination;
		// IF [ avatar is invincible]
		if (this.avatar.getInvincibilityCount() <= 0) {
			destination = this.dijkstra.getBestNeighbor(new Point(this.posX, this.posY));
		} else {
			destination = this.dijkstra.getWorstNeighbor(new Point(this.posX, this.posY));
		}

		if (destination == null) {
			return;
		}

		this.environment.moveAgent(this, destination.x, destination.y);
		this.posX = destination.x;
		this.posY = destination.y;
	}

	private boolean isNextToAvatar() {
		if (this.avatar.getInvincibilityCount() > 0) {
			return false;
		}

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// disable diagonals
				if(i != 0 && j != 0) {
					continue;
				}
				
				int x = this.getPosX() + i;
				int y = this.getPosY() + j;

				if (this.environment.isOutOfBound(x, y) && !this.environment.isTorus()) {
					continue;
				}

				if (this.environment.isOutOfBound(x, y) && this.environment.isTorus()) {
					if (x < 0) {
						x = this.environment.getWidth() - 1;
					}

					if (x >= this.environment.getWidth()) {
						x = 0;
					}

					if (y < 0) {
						y = this.environment.getHeight() - 1;
					}

					if (y >= this.environment.getHeight()) {
						y = 0;
					}
				}

				if (this.environment.getCell(x, y) != null && this.environment.getCell(x, y) instanceof Avatar) {
					return true;
				}
			}
		}
		return false;
	}
}
