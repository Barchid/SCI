package particles;

import java.awt.Color;

import core.Agent;
import core.Environment;

/**
 * 
 * @author samib
 * 
 *         Only Agent in the bouncing MAS
 */
public class Particle extends Agent {

	public Particle(int posX, int posY, int pasX, int pasY, Environment environment) {
		super(posX, posY, pasX, pasY, environment);
		this.setColor(Color.BLACK);
	}

	@Override
	public void decide() {
		int newX = this.posX + this.pasX;
		int newY = this.posY + this.pasY;

		// Collision with one of the environment's border
		if (this.environment.isOutOfBound(newX, newY)) {
			this.moveLimitCollision(newX, newY);
		}
		// No collision with particle
		else if (this.environment.getCell(newX, newY) == null) {
			this.moveNoCollision(newX, newY);
		}
		// Collision with particle
		else {
			this.moveParticleCollision(newX, newY);
		}
	}

	/**
	 * Moves the current particle when there is no collision with another particle
	 * or environment's limit
	 * 
	 * @param x the new x position of the current particle
	 * @param y the new y position of the current particle
	 */
	private void moveNoCollision(int x, int y) {
		this.color = Color.BLACK; // black
		this.environment.moveParticle(this, x, y);
		this.posX = x;
		this.posY = y;
	}

	/**
	 * Moves the current particle when there is a collision with another particle
	 * (meaning that the other particle is located at the (x,y) position in
	 * parameter)
	 * 
	 * @param x
	 * @param y
	 */
	private void moveParticleCollision(int x, int y) {
		this.color = Color.RED; // red

		Particle particle = (Particle) this.environment.getCell(x, y);
		int newPasX = particle.getPasX();
		int newPasY = particle.getPasY();
		particle.reactToCollision(this);
		// exchange directions
		this.pasX = newPasX;
		this.pasY = newPasY;
	}

	/**
	 * Moves the current particle when there is a collision with an environment's
	 * limit (meaning that the (x,y) coordinates are out of bound of the
	 * environment's grid)
	 * 
	 * @param x
	 * @param y
	 */
	private void moveLimitCollision(int x, int y) {
		// IF [the environment is torus] (no collision)
		if (this.environment.isTorus()) {
			this.color = Color.BLACK; // black
			int newX = this.posX;
			int newY = this.posY;
			if (x < 0) {
				newX = this.environment.getWidth() - 1;
			}
			if (x > this.environment.getWidth() - 1) {
				newX = 0;
			}
			if (y < 0) {
				newY = this.environment.getHeigt() - 1;
			}
			if (y > this.environment.getHeigt() - 1) {
				newY = 0;
			}

			this.environment.moveParticle(this, newX, newY);
			this.posX = newX;
			this.posY = newY;
		}
		// ELSE [ there is a collision ]
		else {
			this.color = Color.RED; // red
			this.pasX = this.environment.isXOutOfBound(x) ? -this.pasX : this.pasX;
			this.pasY = this.environment.isYOutOfBound(y) ? -this.pasY : this.pasY;
		}
	}

	/**
	 * Reacts to a collision that has been created by the specified particle
	 * 
	 * @param particle
	 */
	public void reactToCollision(Agent particle) {
		this.color = Color.RED; // red
		// exchange directions
		this.pasX = particle.getPasX();
		this.pasY = particle.getPasY();
	}
}
