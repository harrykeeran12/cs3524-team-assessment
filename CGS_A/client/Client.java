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

    /**
     * This is the constructor for the client.
     * @param host
     * @param port
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Returns an output stream from the client to the server
     * @return an ObjectOutputStream connected to the socket's output stream
     * @throws IOException
     */
    private ObjectOutputStream getStreamToMessenger() throws IOException {
        return new ObjectOutputStream(this.socket.getOutputStream());
    }

    /**
     * Returns an input stream from the server to the client
     * @return an ObjectInputStream connected to the socket's input stream
     * @throws IOException
     */
    private ObjectInputStream getStreamFromMessenger() throws IOException {
        return new ObjectInputStream(this.socket.getInputStream());
    }

    /**
     * Returns the client's message from the terminal and checks if there is a keyword
     * @return String
     */
    private String getMessageFromTerminal() {
        System.out.printf("[%s] Write your message here: \n", this.username);
        String message = null;
        try {
            message = this.scanner.nextLine();
            /* Checks if the first line of the message equals a exit command. */
            if (message.equalsIgnoreCase("exit") || message.split("\\s+")[0].equalsIgnoreCase("exit")) {
                this.connected = false;
            }else if (message.equalsIgnoreCase("logout") || message.split("\\s+")[0].equalsIgnoreCase("logout")) {
                this.connected = false;
            } else if (message.equalsIgnoreCase("rename") || message.split("\\s+")[0].equalsIgnoreCase("rename")){
                try {
                    String args = message.split(" ")[1];
                    this.rename(args);
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Argument for renaming not found, please try again.");
                }
            } else if (message.split("\\s+")[0].equalsIgnoreCase("register")){
                try {
                    this.rename(message.split(" ")[1]);
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Argument for renaming not found, please try again.");
                }
            }
        } catch (NoSuchElementException e) {
            // When the client exit with CTRL-C, this catch avoids error message and close
            // connection
            this.connected = false;
        }
        return message;
    }

    /**
     * Connects the client to the server by creating a socket with host and port number
     * @throws IOException
     * @throws UnknownHostException
     */
    private void connect() {
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

    /**
     * Register a new user
     * @param username
     */
    private void registerUser(String username) {
        if (this.connected == true) {
            try {
                this.username = username;
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

    /**
     * This method enables users to change their usernames
     * @param args
     * @throws ArrayIndexOutOfBoundsException
     */
    private void rename(String args) throws ArrayIndexOutOfBoundsException{
        if (args != null) {
            this.username=args;
        } else {
            System.out.println("Failed renaming");
        }
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
        // System.out.println("\t Started daemon thread.");

    }

    /**
     * This is supposed to run a client
     */
    public void run() {
        this.connect(); // connection to server
        if (this.connected == true) {
            System.out.println("Client connected!");
            try{
                String receivedMessage = (String) this.inputStreamFromMessenger.readObject();
                /* This should ask the user to insert their username. */
                System.out.println(receivedMessage);
                String msg = this.scanner.nextLine();
                this.outputStreamToMessenger.writeObject(msg);
                if (msg.split(" ")[0].equals("register")){
                    this.registerUser(msg.split(" ")[1]);
                }
            }catch (ClassNotFoundException e){
                System.out.println("Failed registering, class not found.");
            } catch (IOException e) {
                System.out.println("I/O error while registering.");
            }catch (ArrayIndexOutOfBoundsException e){
                System.out.println("Argument for renaming not found, please try again.");
            }
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
    }
}
