package hunter;

import java.awt.Color;

import core.Agent;
import core.Environment;

public class Defender extends Agent {
	private int currentLife;
	private HunterAppConfig appConfig;
	private Dijkstra dijkstra;
	private Avatar avatar;

	public Defender(int posX, int posY, Environment environment, HunterAppConfig appConfig, Dijkstra dijkstra,
			Avatar avatar) {
		super(posX, posY, environment);
		this.appConfig = appConfig;
		this.appConfig.getDefendersLife();
		this.currentLife = this.appConfig.getDefendersLife();
		this.color = Color.GREEN;
		this.dijkstra = dijkstra;
		this.avatar = avatar;
	}

	@Override
	public void decide() {
		if (this.currentLife <= 0) {
			this.currentLife = this.appConfig.getDefendersLife();

			int[] coord = this.findFreePlace();
			this.environment.moveAgent(this, coord[0], coord[1]);
			this.posX = coord[0];
			this.posY = coord[1];
			this.dijkstra.breadthFirstSearch(this.avatar.getPosX(), this.avatar.getPosY());
		} else {
			this.currentLife--;
		}
	}

	private int[] findFreePlace() {
		int posX, posY;
		do {
			posX = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeX());
			posY = this.appConfig.getRandom().nextInt(this.appConfig.getGridSizeY());
		} while (this.environment.getCell(posX, posY) != null);

		return new int[] { posX, posY };
	}
}
