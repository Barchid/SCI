import jade.core.Agent;

public class Hello extends Agent {

    protected void setup() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("Oulalah, c'est une exception");
        }
        System.out.println("Hello World! My name is " + getLocalName());

        // Make this agent terminate
        doDelete();
    }
}
