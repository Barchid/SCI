package core;

import java.awt.Color;

public abstract class Agent {
	protected int posX; // current X position of particle
	protected int posY; // current Y position of particle
	protected Color color; // integer that indicates the COLOR of the agent
	protected Environment environment;
	protected boolean isDead;

	public Agent(int posX, int posY, Environment environment) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.color = Color.BLACK; // No collision
		this.environment = environment;
		this.isDead = false;
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


	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}
}
