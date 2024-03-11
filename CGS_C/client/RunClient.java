package client;

import java.io.IOException;
import java.net.UnknownHostException;

public class RunClient {
    public static void main(String[] args) {
        Client client = new Client("localhost", 50000);
        try {
            client.connect();
            System.out.println("Client connected!");
            client.run();
        } catch (UnknownHostException e) {
            System.out.println("Host does not exist. Start the server.");
        } catch (IOException e) {
            System.out.println("Encountered I/O error");
        }
    }
}
