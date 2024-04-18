package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import shared.Message;

import java.lang.IndexOutOfBoundsException;

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

    public void setClientName(String newUsername) {
        this.username = newUsername;
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
                System.out.println(message.toString());
                String keyword = message.getMessageBody().split(" ")[0];
                if (keyword.equalsIgnoreCase("exit")) {
                    /* Send message saying user has been disconnected. */
                    connectionPool.broadcast(
                            new Message(String.format("User %s is being disconnected.", this.username), "[SERVER]"));
                    System.out.println(String.format("User %s is being disconnected.", this.username));
                    break;
                } else if (keyword.equalsIgnoreCase("RENAME")) {
                    /* Send message saying user is going to be renamed. */
                    String oldUsername = this.username;
                    try {
                        String args = message.getMessageBody().split(" ")[1];
                        if (args != null) {
                            connectionPool.broadcast(new Message(
                                    String.format("User %s is being renamed to %s.", oldUsername, args),
                                    "[SERVER]"));
                            setClientName(args);
                        } else {
                            sendMessageToClient(new Message("Unable to complete renaming.", "[SERVER]"));
                        }

                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("Argument for renaming not found.");
                    }
                    // System.out.println("User has been disconnected.");
                } else if (keyword.equalsIgnoreCase("CREATE")) {

                    System.out.println("Create new group here!");
                    /* Get supplementary arguments that are specified by the user. */
                    String args = message.getMessageBody().split(" ")[1];
                    if (args != null) {
                        try {
                            this.connectionPool.groupHandler.createGroup(args);
                            
                        } catch (Exception e) {
                            System.out.println("Problem with creating a new group.");
                            System.out.println(e.getMessage());
                        }
                    } else {
                        this.sendMessageToClient(new Message("Group name was absent. Syntax is CREATE [group name]. ", args));
                    }
                } else if (keyword.equalsIgnoreCase("JOIN")) {
                    String args = message.getMessageBody().split(" ")[1];
                    if ( args!= null){
                        try{
                            this.connectionPool.groupHandler.joinGroup(args, this);
                            connectionPool.broadcast(new Message(
                                    String.format("User %s just joined the group chat %s.", this.username, args),
                                    "[SERVER]"));
                        } catch (Exception e){
                            System.out.format("%s could not join the group chat %s.", this.username, args);
                            System.out.println(e.getMessage());
                        }
                    }
                } else if (keyword.equalsIgnoreCase("LEAVE")) {
                    String args = message.getMessageBody().split(" ")[1];
                    if ( args!= null){
                        try{
                            this.connectionPool.groupHandler.leaveGroup(args, this); 
                            connectionPool.broadcast(new Message(
                                    String.format("User %s just left the group chat %s.", this.username, args),
                                    "[SERVER]"));
                        } catch (Exception e){
                            System.out.format("Problem when leaving the group chat %s.", args);
                            System.out.println(e.getMessage());
                        }
                    }

                } else {
                    this.connectionPool.broadcast(message);
                }
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
