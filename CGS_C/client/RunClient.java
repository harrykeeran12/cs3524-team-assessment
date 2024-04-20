package client;

/**
 * Runs the client.
 */
public class RunClient {
    public static void main(String[] args) {
        Client client = new Client("localhost", 50000);
        client.run();
    }
}
