package agent;

/**
 * @author samib
 *
 *         Agent interface used to describe the standard behaviour of an Agent
 */
public interface Agent {
	/**
	 * Method called by the Scheduler. Method that implements what the Agent does
	 * when it is its turn to interact with the multi agent system.
	 */
	void decide();

	/**
	 * Updates the current Agent's state. Called by another Agent or the Environment when they are interacting
	 */
	void update();
}
