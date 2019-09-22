package environment;

import agent.Particle;
import utils.AppConfig;

public class Environment {
	private AppConfig appConfig;
	private Particle[][] grid;

	public Environment(AppConfig appConfig) {
		super();
		this.appConfig = appConfig;
	}

	/**
	 * Retrieves the particle located in the specified coordinates (x,y)
	 * 
	 * @param x
	 * @param y
	 * @return the particle located in the specified coordinates (x,y) or null if no
	 *         particle found
	 */
	public Particle getCell(int x, int y) {
		return this.grid[x][y];
	}

	/**
	 * Moves the specified particle in the new coordinates (x,y) in parameter
	 * 
	 * @param particle
	 * @param x
	 * @param y
	 */
	public void moveParticle(Particle particle, int x, int y) {
		this.grid[particle.getPosX()][particle.getPosY()] = null;
		this.grid[x][y] = particle;
	}

	/**
	 * Checks whether the specified (x,y) coordinates are out of the grid's bounds
	 * 
	 * @param x
	 * @param y
	 * @return true if the (x,y) coordinates are out of the grid's bounds or else
	 *         false
	 */
	public boolean isOutOfBound(int x, int y) {
		return x < 0 || x >= this.grid.length || y < 0 || y >= this.grid[0].length;
	}

	/**
	 * Checks whether the specified x coordinate is out of the grid's bounds
	 * 
	 * @param x
	 * @return true if the x coordinate is out of the grid's bounds or else false
	 */
	public boolean isXOutOfBound(int x) {
		return x < 0 || x >= this.grid.length;
	}

	/**
	 * Checks whether the specified y coordinate is out of the grid's bounds
	 * 
	 * @param y
	 * @return true if the y coordinate is out of the grid's bounds or else false
	 */
	public boolean isYOutOfBound(int y) {
		return y < 0 || y >= this.grid[0].length;
	}

	public void setGrid(Particle[][] grid) {
		this.grid = grid;
	}

	public int getWidth() {
		return this.grid.length;
	}

	public int getHeigt() {
		return this.grid[0].length;
	}

	public boolean isTorus() {
		return this.appConfig.isTorus();
	}
}
