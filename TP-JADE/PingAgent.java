import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class PingAgent extends Agent {
    protected void setup() {
        // l'ajout d'un one-shot behaviour pour afficher un Hello world :D
        addBehaviour(new OneShotBehaviour(this) {
            public void action() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println("Oulalah, c'est une exception");
                }
                System.out.println("Goeiedag, ik ben het agent van naam " + getLocalName());
            }
        });

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                System.out.println("Cycling");
            }
        });
    }

    /**
     * Inner class RandomBehaviour
     */
    private class RandomBehaviour extends Behaviour {
        private int aleatoire;

        public void action() {
            aleatoire = (int) (Math.random() * 10);
            System.out.println("aleatoire =" + aleatoire);
        }

        public boolean done() {
            return aleatoire == 7;
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }
}