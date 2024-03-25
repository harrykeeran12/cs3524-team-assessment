package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import shared.Message;
import shared.ListenerThread;
/**
 * This is the client class, which takes a string, the host and the port, and
 * tries to connect, to send messages.
 **/
public class Client {

    private Socket socket;
    private String host;
    private int port;
    private String username;
    private ListenerThread listenerThread;
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
        System.out.printf("[%s] Write your message here: \n", this.username);
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
        // test if this username already registered? add this new username to database?
        if (this.connected == true) {

            // System.out.println("[CLIENT] Insert your username :");
            try {
                String receivedMessage = (String) this.inputStreamFromMessenger.readObject();
                System.out.println(receivedMessage);
                /* This should ask the user to insert their username. */
                this.username = this.scanner.nextLine();
                this.outputStreamToMessenger.writeObject(this.username);
                /* This is where you would check where the username is entered. */
                String callback = (String) this.inputStreamFromMessenger.readObject();
                System.out.println(callback);
            } catch (IOException e) {
                System.out.println("Error while registering");
                this.connected = false;
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found");
            }
        } else {
            System.out.println("User unable to be registered.");
        }
    }

    /** For a user to login, they must already be in the database. */
    private void loginUser() {
        // if username already registered in the database, then connection
    }

    /**
     * This method only runs when this.connected is false. This logs out the user
     * and closes the scanner, the listener thread and the socket connection.
     */
    private void logoutUser() {
        System.out.println(this.username + " is logging out...");
        try {
            this.scanner.close();
            this.socket.close();
            if (listenerThread != null) {
                this.listenerThread.interrupt();
            }
        } catch (IOException e) {
            System.out.println("Failed to log out..."); // socket didn't closed itself successfully
        }
    }

    /** This should start a thread that reads any incoming messages. */
    private void listenToServer() {
        this.listenerThread = new ListenerThread(inputStreamFromMessenger, this.connected);
        this.listenerThread.setDaemon(true);
        // keeps reading from server and print out the messages from other users
        this.listenerThread.start();
        System.out.println("\t Started daemon thread.");

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

                    System.out.println("\n");
                }
                try {
                    this.outputStreamToMessenger.writeObject(new Message(mssg, this.username));

                } catch (IOException e) {
                    System.out.println("Problem with sending a new message.");
                }
            }
            this.logoutUser();
        }
        // this.loginUser();

    }
}
