package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import shared.Message;

/**
 * This is the client class, which takes a string, the host and the port, and
 * tries to connect, to send messages.
 **/
public class Client {

    private Socket socket;
    private String host;
    private int port;
    private String username;
    private Thread listenerThread;
    private Scanner scanner;
    private ObjectInputStream inputStreamFromMessenger;
    private ObjectOutputStream outputStreamToMessenger;
    private Boolean connected;

    /** This is the constructor for the client. */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        // this.socket = null;
        // this.username = null;
        // this.listenerThread = null;
        // this.scanner = null;
        // this.inputStreamFromMessenger = null;
        // this.outputStreamToMessenger = null;
        // this.connected = null;
    }

    private ObjectOutputStream getStreamToMessenger() throws IOException {
        return new ObjectOutputStream(this.socket.getOutputStream());
    }

    private ObjectInputStream getStreamFromMessenger() throws IOException {
        return new ObjectInputStream(this.socket.getInputStream());
    }

    private String getMessageFromTerminal() {
        System.out.println("[CLIENT] Write your message here: ");
        String message = null;
        try {
            message = this.scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) {
                this.connected = false;
            }
        } catch (NoSuchElementException e) {
            // When the client exit with CTRL-C, this catch avoids error message and close
            // connection
            this.connected = false;
        }
        return message;
    }

    private void connect() {
        /* Connect the client to the server */
        try {
            this.socket = new Socket(this.host, this.port);
            this.outputStreamToMessenger = this.getStreamToMessenger();
            this.inputStreamFromMessenger = this.getStreamFromMessenger();
            this.scanner = new Scanner(System.in);
            this.connected = true;
        } catch (UnknownHostException e) {
            System.err.println("Host does not exist.");
            this.connected = false;
        } catch (IOException e) {
            System.err.println("Connection to server has failed.");
            this.connected = false;
        }
    }

    private void registerUser() {
        if (!this.connected)
            return; // if client not successfully connected to the server, then no register possible
        // test if this username already registered? add this new username to database?
        System.out.println("Insert your user name :");
        try {
            this.username = this.scanner.nextLine();
            String receivedMessage = (String) this.inputStreamFromMessenger.readObject();
            System.out.println(receivedMessage);
            this.outputStreamToMessenger.writeObject(this.username);
            System.out.println("Registered successfully");
        } catch (IOException e) {
            System.out.println("Error while registering");
            this.connected = false;
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
        }
    }

    private void loginUser() {
        // if username already registered in the database, then connection
    }

    private void logoutUser() {
        System.out.println(this.username + "is logging out...");
        try {
            this.scanner.close();
            this.listenerThread.interrupt();
            this.socket.close();
        } catch (IOException e) {
            System.out.println("Failed to log out..."); // socket didn't closed itself successfully
        }
    }

    private void listenToServer() {
        this.listenerThread = new Thread();
        this.listenerThread.setDaemon(true);
        this.listenerThread.start();
        System.out.println("\t Started daemon thread.");
        // keeps reading from server and print out the messages from other users
        while (true) {
            try {
                Message receivedMessage = (Message) this.inputStreamFromMessenger.readObject();
                System.out.println(receivedMessage.toString());
            } catch (ClassNotFoundException e) {
                System.err.println("Could not deserialise the message.");
            } catch (IOException e) {
                if (!this.connected) {
                    // If the program is not exited continue listening
                    System.err.println("Failed while listening to server.");
                } else {
                    // Otherwise, stop listening
                    break;
                }
            }
        }

    }

    public void run() {
        this.connect(); // connection to server
        // this.listenToServer();
        // String receivedMessage = (String) this.inputStreamFromMessenger.readObject();
        // System.out.println(receivedMessage);
        if (this.connected == true) {
            System.out.println("Client connected!");
            this.registerUser();
            this.listenToServer();
            while (this.connected) {
                String mssg = this.getMessageFromTerminal();
                if (mssg != null) {
                    // this.sendUserMessage(mssg);
                    System.out.println("message to test");
                }
                try {
                    Message echoMessenger = (Message) this.inputStreamFromMessenger.readObject();
                    System.out.println(echoMessenger.toString());
                } catch (IOException e) {
                    System.out.println("I/O error");
                } catch (ClassNotFoundException e) {
                    System.out.println("Message could not be deserialised.");
                }
            }
            this.logoutUser();
        }
        // this.loginUser();

    }
}
