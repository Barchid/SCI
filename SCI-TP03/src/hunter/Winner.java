package hunter;

import java.awt.Color;

import core.Agent;
import core.Environment;

public class Winner extends Agent {

	public Winner(int posX, int posY, Environment environment) {
		super(posX, posY, environment);
		this.color = Color.PINK;
	}

	@Override
	public void decide() {

	}

}
