import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class CounterAgent extends Agent {
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private AID friend;

	protected void setup() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Oulalah, c'est une exception");
		}
		System.out.println(this.getLocalName() + " Ready");

		// Récupérer le friend
		Object[] args = this.getArguments();
		if (args != null && args.length > 0) {
			String friend = (String) args[0];
			this.friend = new AID(friend, AID.ISLOCALNAME);
		} else {
			// Make the agent terminate immediately
			System.out.println("No friend specified");
			doDelete();
		}

		this.addBehaviour(new CounterBehaviour(this));
	}

	private class CounterBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 8624770352595040341L;

		public CounterBehaviour(Agent a) {
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
			
			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
			reply.addReceiver(friend);
			
			// Décrémenter le truc
			int count = 0;
			try {
				count = Integer.parseInt(message.getContent());
			} catch (NumberFormatException e) {
				System.out.println("Not and integer");
				return;
			}

			if (count <= 0) {
				System.out.println("Counter done");
				return;
			}
			count--;
			
			reply.setContent("" + count);
			send(reply);
		}
	} // END of inner class CounterBehavior
}
