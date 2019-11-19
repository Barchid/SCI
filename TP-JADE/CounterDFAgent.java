import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.util.Logger;

public class CounterDFAgent extends Agent {
	private static final long serialVersionUID = -7401885277411453115L;
	private Logger myLogger = Logger.getMyLogger(getClass().getName());
	private List<AID> friends = new ArrayList<AID>();

	private static final String COUNTER_SERVICE = "Counter";

	protected void setup() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.out.println("Oulalah, c'est une exception");
		}
		System.out.println(this.getLocalName() + " Ready");

		// S'enregistrer dans le service de counter
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(COUNTER_SERVICE);
		sd.setName(this.getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		this.addBehaviour(new CounterBehaviour(this));
	}

	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Printout a dismissal message
		System.out.println("Counter " + getAID().getName() + " terminating.");
	}

	private class CounterBehaviour extends CyclicBehaviour {

		private static final long serialVersionUID = 8624770352595040341L;

		public CounterBehaviour(Agent a) {
			super(a);
		}

		// pattern utilisÃ© pour la réception de message, fortement recommendé par
		// jade
		public void action() {
			ACLMessage msg = myAgent.receive();
			if (msg != null) {

				// Choisir ce que fait l'agent en fonction de la performative du message reçu
				switch (msg.getPerformative()) {
				case ACLMessage.PROPOSE: // PROPOSE lance la procédure de décrémentation
					this.initFriends();
					break;
				case ACLMessage.INFORM: // INFORM décrémente le counter reçu dans le contenu du message
					this.decrementCounter(msg);
					break;
				case ACLMessage.FAILURE: // FAILURE indique que la source du message reçu doit être retirée de la liste
											// des friends
					this.removeFriend(msg);
					break;
				}
			} else {
				block();
			}
		}

		/**
		 * Retire de la liste des friends l'agent qui a envoyé le message en paramètres
		 * 
		 * @param msg
		 */
		private void removeFriend(ACLMessage msg) {
			AID toRemove = msg.getSender();
			if (friends.contains(toRemove)) {
				friends.remove(toRemove);
			}
		}

		/**
		 * Enregistre les autres agents counter enregistrés sur le DF
		 */
		private void initFriends() {
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType(COUNTER_SERVICE);
			template.addServices(sd);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				friends.clear();
				for (int i = 0; i < result.length; ++i) {
					if (!result[i].getName().equals(getAID())) {
						friends.add(result[i].getName());
					}
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		/**
		 * méthode appelée quand le counter agent a perdu (il a reçu un 0). Il meurt et
		 * choisi un nouveau compteur entre 1 et 10 qu'il communique à un gars
		 */
		private void lose() {
			// dire à tout le monde que je suis mort
			ACLMessage deathInfo = new ACLMessage(ACLMessage.FAILURE);
			for (AID friend : friends) {
				deathInfo.addReceiver(friend);
			}
			deathInfo.setContent("g perdu");
			send(deathInfo);

			// communiquer à un mec au hasard le nouveau compteur
			int count = new Random().nextInt(10) + 1; // compteur entre 1 et 10
			ACLMessage newCount = new ACLMessage(ACLMessage.INFORM);
			newCount.addReceiver(friends.get(new Random().nextInt(friends.size()))); // random receiver
			newCount.setContent("" + count);
			send(newCount);

			// deregister and die
			doDelete();
		}

		private void decrementCounter(ACLMessage message) {
			myLogger.log(Logger.INFO, "Received message from " + message.getSender().getLocalName());

			ACLMessage reply = new ACLMessage(ACLMessage.INFORM);

			if (friends == null) {
				System.out.println("No friends known");
				return;
			}

			// Choose one of the receiver
			reply.addReceiver(friends.get(new Random().nextInt(friends.size())));

			// Décrémenter le truc
			int count = 0;
			try {
				count = Integer.parseInt(message.getContent());
			} catch (NumberFormatException e) {
				System.out.println("Not and integer");
				return;
			}

			if (count <= 0) {
				System.out.println("J'ai reçu un 0, je meurs");
				this.lose();
				return;
			}
			count--;

			reply.setContent("" + count);
			send(reply);
		}
	} // END of inner class CounterBehavior
}
