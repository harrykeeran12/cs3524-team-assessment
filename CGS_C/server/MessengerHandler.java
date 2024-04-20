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
    public void registerUser(String username) throws IOException, ClassNotFoundException, ArrayIndexOutOfBoundsException {
        try {
            this.username = username;
            /* Send a callback, back to the user saying that they have registered. */
            this.streamToClient.writeObject(String.format("User %s successfully registered!", this.username));
            System.out.println(String.format("User %s joined the chat.\n", this.username));
        } catch (IOException e) {
            System.out.println("User " + this.username + "failed to register.");
        }
        this.connectionPool.broadcast(this.getUserMessage(String.format("User %s joined the chat.\n", this.username)));
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
            this.streamToClient.writeObject("Please first register by typing 'register username'.");
            String msg = (String) this.streamFromClient.readObject();
            if (msg.split(" ")[0].equals("register")) {
                try{
                    String newUsername = msg.split(" ")[1];
                    if (!connectionPool.containsUsername(newUsername)){
                        this.registerUser(newUsername);
                    }
                    else sendMessageToClient(new Message("Name is already in use, please use another one by typing 'rename username'.", "[SERVER]"));
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Argument for renaming not found, waiting for client to try again...");
                }
                
            }
            while (true) {
                Message message = (Message) streamFromClient.readObject();
                System.out.println(message.toString());
                String keyword = message.getMessageBody().split(" ")[0];
                if (keyword.equalsIgnoreCase("logout") || keyword.equalsIgnoreCase("exit") || keyword.equalsIgnoreCase("unregister")) {
                    /* Send message saying user has been disconnected. */
                    connectionPool.broadcast(
                            new Message(String.format("User %s is being disconnected.", this.username), "[SERVER]"));
                    System.out.println(String.format("User %s is being disconnected.", this.username));
                    break;
                } else if (keyword.equalsIgnoreCase("register")){
                    try{
                        String newUsername = message.getMessageBody().split(" ")[1];
                        if (newUsername != null && !connectionPool.containsUsername(newUsername)){
                            setClientName(newUsername);
                            sendMessageToClient(new Message("You successfully registered and joined the chat.", "[SERVER]"));
                            this.connectionPool.broadcast(this.getUserMessage(String.format("User %s joined the chat.\n", this.username)));
                            System.out.println(String.format("User %s successfully registered and joined the chat.\n", this.username));
                        }else {
                            sendMessageToClient(new Message("Name already in use, please use another one again.", "[SERVER]"));
                        }
                    } catch (ArrayIndexOutOfBoundsException e){
                        System.out.println("Argument for renaming not found.");
                    }
                } else if (keyword.equalsIgnoreCase("rename")) {
                    /* Send message saying user is going to be renamed. */
                    String oldUsername = this.username;
                    try {
                        String args = message.getMessageBody().split(" ")[1];
                        if (args != null && !connectionPool.containsUsername(args)) {
                            connectionPool.broadcast(new Message(
                                    String.format("User %s is being renamed to %s.", oldUsername, args),
                                    "[SERVER]"));
                            setClientName(args);
                        } else {
                            sendMessageToClient(new Message("Name already in use, please use another one again.", "[SERVER]"));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Argument for renaming not found.");
                    }
                    // System.out.println("User has been disconnected.");
                } else {
                    connectionPool.broadcast(message);
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
