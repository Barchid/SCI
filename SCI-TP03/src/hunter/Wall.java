package hunter;

import java.awt.Color;

import core.Agent;
import core.Environment;

public class Wall extends Agent {

	public Wall(int posX, int posY, Environment environment) {
		super(posX, posY, environment);
		this.color = Color.BLACK;
	}

	@Override
	public void decide() {
	}

}
