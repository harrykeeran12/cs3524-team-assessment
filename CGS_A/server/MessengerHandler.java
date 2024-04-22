package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

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

    /**
     * MessengerHandler constructor, which provides an interface for a client to
     * handle a message.
     * @param socket
     * @param connectionPool
     */
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

    /**
     * Registers a new user, and broadcasts the name to everyone.
     * @param username
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws ArrayIndexOutOfBoundsException
     */
    public void registerUser(String username)
            throws IOException, ClassNotFoundException, ArrayIndexOutOfBoundsException {
        try {
            if (username!= null && !connectionPool.containsUsername(username)){
                /*Checks if the username entered by client is unique. */
                this.username = username;
                /* Send a callback, back to the user saying that they have registered. */
                this.streamToClient.writeObject(String.format("User %s successfully registered!", this.username));
                System.out.println(String.format("User %s joined the chat.\n", this.username));
                this.connectionPool.broadcast(this.getUserMessage(String.format("User %s joined the chat.\n", this.username)));
            } else {
                this.streamToClient.writeObject("Name already in use, please try again.");
            }
        } catch (IOException e) {
            System.out.println("User " + this.username + "failed to register.");
        }
    }

    /**
     * Gets the client's username.
     * @return
     */
    public String getClientName() {
        return this.username;
    }

    /**
     * Sets the client's username
     * @param newUsername
     */
    public void setClientName(String newUsername) {
        this.username = newUsername;
    }

    /**
     * Gets an instance of the client's socket
     * @return
     */
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

    /** Closes the connection with the server. **/
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

    /**
     * Sends a message specifically to the client.
     * @param message
     */
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
                try {
                    String newUsername = msg.split(" ")[1];
                    this.registerUser(newUsername);
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Argument for renaming not found, waiting for client to try again...");
                } 
            }
            while (true) {
                Message message = (Message) streamFromClient.readObject();

                String keyword = message.getMessageBody().split(" ")[0];
                if (keyword.equalsIgnoreCase("exit") || keyword.equalsIgnoreCase("logout")
                        || keyword.equalsIgnoreCase("unregister")) {
                    /* Send message saying user has been disconnected. */
                    connectionPool.broadcast(
                            new Message(String.format("User %s is being disconnected.", this.username), "[SERVER]"));
                    System.out.println(String.format("User %s is being disconnected.", this.username));
                    break;
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
                            sendMessageToClient(
                                    new Message("Name already in use, please use another one again.", "[SERVER]"));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Argument for renaming not found.");
                    }
                    // System.out.println("User has been disconnected.");
                } else if (keyword.equalsIgnoreCase("register")) {
                    try {
                        String newUsername = message.getMessageBody().split(" ")[1];
                        if (newUsername != null && !connectionPool.containsUsername(newUsername)) {
                            /* Using setClientName() instead of registerUsername() because registerUsername() sends 
                             * a String to the client that cannot be casted into a Message in the Client class.
                             */
                            this.setClientName(newUsername);
                            sendMessageToClient(new Message("You have successfully registered and joined the chat.", "[SERVER]"));
                            this.connectionPool.broadcast(this.getUserMessage(String.format("User %s joined the chat.\n", this.username)));
                            System.out.println(String.format("User %s successfully registered and joined the chat.\n", this.username));
                        } else {
                            sendMessageToClient(new Message(
                                    "This name is already in use, please use another one by typing 'rename username'.",
                                    "[SERVER]"));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Argument for renaming not found.");
                    }
                } else if (keyword.equalsIgnoreCase("create")) {
                    // System.out.println("Create new group here!");
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
                        this.sendMessageToClient(
                                new Message("Group name was absent. Syntax is create [group name]. ", args));
                    }
                } else if (keyword.equalsIgnoreCase("join")) {
                    String args = message.getMessageBody().split(" ")[1];
                    if (args != null) {
                        try {
                            this.connectionPool.groupHandler.joinGroup(args, this);
                            connectionPool.broadcast(new Message(
                                    String.format("User %s just joined the group chat %s.", this.username, args),
                                    "[SERVER]"));
                        } catch (Exception e) {
                            System.out.printf("%s could not join the group chat %s.", this.username, args);
                            System.out.println(e.getMessage());
                        }
                    }
                } else if (keyword.equalsIgnoreCase("leave")) {
                    String args = message.getMessageBody().split(" ")[1];
                    if (args != null) {
                        try {
                            this.connectionPool.groupHandler.leaveGroup(args, this);
                            connectionPool.broadcast(new Message(
                                    String.format("User %s just left the group chat %s.", this.username, args),
                                    "[SERVER]"));
                        } catch (Exception e) {
                            System.out.format("Problem when leaving the group chat %s.", args);
                            System.out.println(e.getMessage());
                        }
                    }
                } else if (keyword.equalsIgnoreCase("remove")) {
                    String args = message.getMessageBody().split(" ")[1];
                    if (args != null) {
                        try {
                            this.connectionPool.groupHandler.removeGroup(args);
                            connectionPool.broadcast(new Message(
                                    String.format("User %s just removed the group %s.", this.username, args),
                                    "[SERVER]"));
                        } catch (Exception e) {
                            System.out.format("Problem when removing the group chat %s.", args);
                            System.out.println(e.getMessage());
                        }
                    }
                } else if (keyword.equalsIgnoreCase("send")) {
                    /*
                     * This function sends a message either to another user or a group name that the
                     * user that sent the message is part of.
                     */
                    String messageArray[] = message.getMessageBody().split(" ", 3);
                    String sender = this.username;
                    String receiver = messageArray[1];
                    String contents = messageArray[2];
                    /* Check if the name is part of the connection pool. */
                    if (!this.username.equalsIgnoreCase(receiver)) {
                        if (this.connectionPool.containsUsername(receiver) == true) {
                            // System.out.println("This name is a valid name.");
                            // sendMessageToClient(new Message(String.format("Sending a message to %s",
                            // name), "[SERVER]"));
                            this.connectionPool.sendToUser(receiver, new Message(contents, this.username));
                        }
                        /* Check if the name is a group name. */
                        else if (this.connectionPool.checkGroupName(receiver) == true) {
                            try {
                                Group groupToBroadcast = this.connectionPool.groupHandler.findGroup(receiver);
                                /* Check if sender is part of group. */
                                if (groupToBroadcast.checkMembership(sender) == true) {
                                    groupToBroadcast.broadcastGroup(new Message(contents, sender));
                                } else {
                                    sendMessageToClient(new Message("User is not part of the group."));
                                }

                            } catch (Exception e) {
                                sendMessageToClient(new Message("Error sending a message to the group."));
                            }
                        } else {
                            sendMessageToClient(new Message("This user does not exist."));
                        }
                    } else {

                        sendMessageToClient(new Message("Cannot send message to yourself."));
                    }

                } else if (keyword.equalsIgnoreCase("list")) {
                    // System.out.println("LIST");
                    ArrayList<String> userList = this.connectionPool.getAllUsernames();
                    ArrayList<String> groupList = this.connectionPool.getGroupNameAndOccupancy();
                    if (userList.size() == 0) {
                        sendMessageToClient(new Message("No users in list."));
                    } else {
                        sendMessageToClient(new Message(String.format("Connected users: %s\n",
                                String.join(" ", userList))));
                    }
                    if (groupList.size() == 0) {
                        sendMessageToClient(new Message("No groups have been created."));

                    } else {
                        sendMessageToClient(new Message(String.format("Group names: %s",
                                String.join(" ", groupList))));
                    }

                } 
                else if(keyword.equalsIgnoreCase("subscribe")) {
                    /* Subscribes the user to a particular topic. */
                    String args = message.getMessageBody().split(" ")[1];
                    try {
                        this.connectionPool.topicHandler.subscribeToTopic(args, this);
                    } catch (Exception e) {
                        System.out.println("Problem with subscribing.");
                        System.out.println(e.getMessage());
                    }
                    
                }
                else if (keyword.equalsIgnoreCase("unsubscribe")) {
                    /* Unsubscribes the user from a particular topic. */
                    String args = message.getMessageBody().split(" ")[1];
                    try {
                        this.connectionPool.topicHandler.unsubscribeToTopic(args, this);
                    } catch (Exception e) {
                        System.out.println("Problem with unsubscribing.");
                        System.out.println(e.getMessage());
                    }
                }
                else if (keyword.equalsIgnoreCase("topic")) {
                    /* Gets the next argument. */
                    String args = message.getMessageBody().split(" ")[1];
                    try {
                        this.connectionPool.topicHandler.createTopic(args);
                    } catch (Exception e) {
                        System.out.println("Problem with creating a new topic.");
                        System.out.println(e.getMessage());
                    }
                    /* Creates a new topic. */
                }
                else if (keyword.equalsIgnoreCase("topics")) {
                    /* Lists all the current topics that the server has registered. */
                    Set<String> topicList = this.connectionPool.topicHandler.topicList();
                    if (topicList.size() == 0) {
                        System.out.println("No topics have been created.");
                        sendMessageToClient(new Message("No topics have been created."));
                    } else {
                        System.out.println(String.format("Topics are: %s", topicList));
                        sendMessageToClient(new Message(String.format("Topics are: %s", topicList)));
                    }
                    
                }
                else {
                    System.out.println(message.toString());
                    try {
                        Set<String> foundTopics = message.checkMessageBody(this.connectionPool.topicHandler.topicList());
                        for (String foundTopic : foundTopics) {
                            Group groupToBroadcast = this.connectionPool.topicHandler.getTopicGroup(foundTopic);
                            groupToBroadcast.broadcastGroup(message);
                        }
                        
                    } catch (Exception e) {
                        System.out.println("Problem with scanning message body.");
                    }
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
