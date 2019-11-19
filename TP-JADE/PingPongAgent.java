import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class PingPongAgent extends Agent {
	private Logger myLogger = Logger.getMyLogger(getClass().getName());

	protected void setup() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Oulalah, c'est une exception");
		}
		System.out.println(this.getLocalName() + " Ready");
		
		this.addBehaviour(new WaitPingPongBehaviour(this));
	}

	private class WaitPingPongBehaviour extends CyclicBehaviour {

		public WaitPingPongBehaviour(Agent a) {
			super(a);
		}

		// pattern utilisÃ© pour la réception de message, fortement recommendé par
		// jade
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// message received, process it...
				this.reactToMsg(msg);

			} else {
				block();
			}
		}

		private void reactToMsg(ACLMessage message) {
			switch (message.getContent()) {
			case "Ping":
				System.out.println("Pong");
				break;
			case "Pong":
				System.out.println("Ping");
				break;
			default:
				System.out.println("Wablief ?");
				break;
			}
		}
	} // END of inner class WaitPingPongBehaviour
}
