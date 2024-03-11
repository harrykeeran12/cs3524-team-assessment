package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = null;
    }

    private ObjectOutputStream getStreamToMessenger() throws IOException {
        return new ObjectOutputStream(this.socket.getOutputStream());
    }

    private ObjectInputStream getStreamFromMessenger() throws IOException {
        return new ObjectInputStream(this.socket.getInputStream());
    }

    public String getMessageFromTerminal() {
        Scanner input = new Scanner(System.in);
        System.out.println("Write your message here: ");
        String message = input.nextLine();
        input.close();
        return message;
    }

    public void connect() throws IOException, UnknownHostException {
        this.socket = new Socket(this.host, this.port);
    }

    public void run() throws IOException, UnknownHostException {
        try {
            ObjectOutputStream outputStreamToMessenger = this.getStreamToMessenger();
            ObjectInputStream inputStreamFromMessenger = this.getStreamFromMessenger();
            Scanner input = new Scanner(System.in);
            while (true) {
                System.out.println("Write your message here: ");
                String message = input.nextLine();
                outputStreamToMessenger.writeObject(message);
                if (message.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnecting...");
                    input.close();
                    socket.close();
                    break;
                }
                String echoMessenger = (String) inputStreamFromMessenger.readObject();
                System.out.println(echoMessenger);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Received unsupported object from Messenger");
        }
    }
}
