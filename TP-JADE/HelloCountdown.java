import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class HelloCountdown extends Agent {

    protected void setup() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Oulalah, c'est une exception");
        }
        System.out.println("Hello World! My name is " + getLocalName());

        addBehaviour(new TickerBehaviour(this, 5000) {
            protected void onTick() {
                System.out.println("Agent " + myAgent.getLocalName() + ": tick=" + getTickCount());
            }
        });

    }
}
