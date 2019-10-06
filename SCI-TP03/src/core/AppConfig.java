package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

/**
 * 
 * @author samib
 *
 *         Class used to retrieve and distribute the configurable parameters of
 *         the application (contained in a Properties file)
 */
public class AppConfig {
	protected Properties properties;
	private boolean isTorus; // flag that indicates whether the environment is a torus or a limited
								// square/rectangle
	private int gridSizeX; // number of cells for the Environment
	private int gridSizeY; // number of cells for the Environment
	private int boxSize; // size of a cell's side in pixel
	private int delay; // time of slowdown after every turn (in miliseconds)
	private int scheduling; // scheduling mode for the multi agent system
	private int nbTicks; // Number of ticks (infinite if == 0)
	private boolean isGrid; // indicates whether the grid is displayed
	private boolean hasTrace; // indicates whether there is a trace in standard output to show the evolution
								// of the system
	private int seed; // Seed to regenerate the pseudo-random of the placement, 0 for true randomness
	private int nbParticles; // number of particles in the grid

	private Random random; // random generator for the application

	// Constants of scheduling
	public static final int SCHED_FAIR = 0;
	public static final int SCHED_RAND = 1;
	public static final int SCHED_SEQ = 2;

	public AppConfig(String propertiesPath) throws Exception {
		try (InputStream file = new FileInputStream(propertiesPath)) {
			this.properties = new Properties();
			this.properties.load(file);
		} catch (IOException ex) {
			throw new Exception("Error while reading the properties file. Cannot launch the Application.");
		}

		this.gridSizeX = Integer.parseInt(this.properties.getProperty("gridSizeX"));
		this.gridSizeY = Integer.parseInt(this.properties.getProperty("gridSizeY"));
		this.boxSize = Integer.parseInt(this.properties.getProperty("boxSize"));
		this.delay = Integer.parseInt(this.properties.getProperty("delay"));
		this.scheduling = Integer.parseInt(this.properties.getProperty("scheduling"));
		this.seed = Integer.parseInt(this.properties.getProperty("seed"));
		this.nbParticles = Integer.parseInt(this.properties.getProperty("nbParticles"));
		this.nbTicks = Integer.parseInt(this.properties.getProperty("nbTicks"));
		this.isGrid = Boolean.parseBoolean(this.properties.getProperty("isGrid"));
		this.isTorus = Boolean.parseBoolean(this.properties.getProperty("isTorus"));
		this.hasTrace = Boolean.parseBoolean(this.properties.getProperty("hasTrace"));

		if (this.seed == 0) {
			this.random = new Random();
		} else {
			this.random = new Random(this.seed);
		}
	}

	public boolean isTorus() {
		return isTorus;
	}

	public int getGridSizeX() {
		return gridSizeX;
	}

	public int getGridSizeY() {
		return gridSizeY;
	}

	public int getBoxSize() {
		return boxSize;
	}

	public int getDelay() {
		return delay;
	}

	public int getScheduling() {
		return scheduling;
	}

	public int getNbTicks() {
		return nbTicks;
	}

	public boolean isGrid() {
		return isGrid;
	}

	public boolean hasTrace() {
		return hasTrace;
	}

	public int getSeed() {
		return seed;
	}

	public int getNbParticles() {
		return nbParticles;
	}

	public Random getRandom() {
		return random;
	}
	
	public void setDelay(int delay) {
		this.delay = delay;
	}
}
