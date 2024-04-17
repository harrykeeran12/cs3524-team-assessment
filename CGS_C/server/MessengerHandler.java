package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import shared.Message;

/**
 * A message handler class, which makes sure messages can be sent to all users.
 * This implements the Runnable interface, allowing for multi-threading
 * capability.
 **/
public class MessengerHandler implements Runnable {

    private Socket socket;
    private ObjectOutputStream streamToClient;
    private ObjectInputStream streamFromClient;
    private String username;
    private ConnectionPool connectionPool;
    /** Reserved keywords for the server to handle in specific ways. */
    private static String[] keywords = { "REGISTER", "LOGIN", "LOGOUT" };

    /**
     * MessengerHandler constructor, which provides an interface for a client to
     * handle a message.
     **/
    public MessengerHandler(Socket socket, ConnectionPool connectionPool) {
        this.socket = socket;
        this.connectionPool = connectionPool;

        try {
            this.streamFromClient = new ObjectInputStream(socket.getInputStream());
            this.streamToClient = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("I/O error");
        }
    }

    /** Registers a new user, and broadcasts the name to everyone. **/
    public void registerUser() throws IOException, ClassNotFoundException {
        try {
            this.streamToClient.writeObject("Please enter a username.");
            this.username = (String) this.streamFromClient.readObject();
            /* Send a callback, back to the user saying that they have registered. */
            this.streamToClient.writeObject(String.format("User %s successfully registered!", this.username));
            System.out.println(String.format("User: %s joined the chat.\n", this.username));
        } catch (IOException e) {
            System.out.println("User " + this.username + "failed to register.");
        }
        this.connectionPool.broadcast(this.getUserMessage(String.format("User: %s joined the chat.\n", this.username)));
    }

    /**
     * Get the client's username.
     * 
     */
    public String getClientName() {
        return this.username;
    }

    /** Get an instance of the client's socket */
    public Socket getClientSocket() {
        return this.socket;
    }

    /**
     * A method to create a new instance of the message class.
     * 
     * @param messageBody
     * @return Message
     */
    public Message getUserMessage(String messageBody) {
        return new Message(messageBody, this.username);
    }

    /** Close the connection with the server. **/
    private void close() {
        this.connectionPool.removeUser(this);
        try {
            this.socket.close();
        } catch (IOException | NullPointerException e) {
            // There was an I/O exception or the socket was not instantiated
            // In either case, do nothing.
        } finally {
            this.connectionPool.broadcast(
                    this.getUserMessage("just left the chat."));
        }
    }

    /** Send a message specifically to the client. **/
    public void sendMessageToClient(Message message) {
        try {
            streamToClient.writeObject(message);
        } catch (IOException e) {
            System.out.println("Failed sending the message " + message.getMessageBody() + " to " + this.username);
        }
    }

    /**
     * An overridden method that contains the logic to check if a keyword has been
     * written.
     **/
    @Override
    public void run() {
        try {
            this.registerUser();

            while (true) {
                Message message = (Message) streamFromClient.readObject();
                // String messageBody = message.getMessageBody();
                // Overwrite this.username with the one contained in the message
                // this.username = message.getUser();
                System.out.println(message.toString());
                // for (String keyword : keywords) {
                // /* Check every keyword. */
                // if (messageBody.split(" ")[0].equalsIgnoreCase(keyword)) {
                // /* This checks if the first part of the message contains a keyword. */
                // if (keyword.equals("REGISTER")) {
                // } // needs to define a Register method
                // else if (keyword.equals("LOGIN")) {
                // } // login method
                // else { // keyword=LOGOUT
                // this.connectionPool.removeUser(this);
                // socket.close();
                // this.connectionPool.broadcast(this.getUserMessage("just left the chat"));
                // }
                // System.out.println("Contains a keyword.");
                // System.out.printf("%s \n", keyword);
                // }
                // }

                // if (messageBody.equalsIgnoreCase("exit"))
                // send message to all other clients
                connectionPool.broadcast(message);
                // break;
            }
        } catch (IOException e) {
            System.out.println("Communication interrupted with " + this.username);
        } catch (ClassNotFoundException e) {
            System.out.println("Communication interrupted with " + this.username);
        } finally {
            this.close();
        }
    }
}
