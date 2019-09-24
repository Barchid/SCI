package core;

import java.awt.Color;

public abstract class Agent {
	protected int posX; // current X position of particle
	protected int posY; // current Y position of particle
	protected int pasX; // current X direction of particle
	protected int pasY; // current Y direction of particle
	protected Color color; // integer that indicates the COLOR of the agent
	protected Environment environment;

	public Agent(int posX, int posY, int pasX, int pasY, Environment environment) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.pasX = pasX;
		this.pasY = pasY;
		this.color = Color.BLACK; // No collision
		this.environment = environment;
	}

	abstract public void decide();

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getPasX() {
		return pasX;
	}

	public void setPasX(int pasX) {
		this.pasX = pasX;
	}

	public int getPasY() {
		return pasY;
	}

	public void setPasY(int pasY) {
		this.pasY = pasY;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
