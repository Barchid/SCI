import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class PingPongReplyAgent extends Agent {
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
				this.reactToMsg(msg);

			} else {
				block();
			}
		}

		private void reactToMsg(ACLMessage message) {
			myLogger.log(Logger.INFO, "Received message from " + message.getSender().getLocalName());
			ACLMessage reply = message.createReply();
			reply.setPerformative(ACLMessage.INFORM);
			
			switch (message.getContent()) {
			case "Ping":
				reply.setContent("Pong");
				break;
			case "Pong":
				reply.setContent("Ping");
				break;
			default:
				reply.setContent("Wablief ?");
				break;
			}
			
			send(reply);
		}
	} // END of inner class WaitPingPongBehaviour
}
