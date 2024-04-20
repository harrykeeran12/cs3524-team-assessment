package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import shared.Message;

/** This class houses all the current connections of the server. **/

public class ConnectionPool {
    private List<MessengerHandler> connections = new ArrayList<>();
    public GroupHandler groupHandler = new GroupHandler();
    public TopicHandler topicHandler = new TopicHandler();

    /** Adds a new connection. **/
    public void addConnection(MessengerHandler handler) {
        System.out.println("\t [SERVER]: Added a new connection to the handler.");
        connections.add(handler);
    }

    /** Remove a user from the connection pool. **/
    public void removeUser(MessengerHandler handler) {
        System.out.println("\t [SERVER]: Removed a connection from the handler.");
        connections.remove(handler);
    }

    /** Checks if a username is already being used when registering. **/
    public boolean containsForRegister(String username) {
        int occurrence = 0;
        for (MessengerHandler handler : this.connections) {
            if (handler.getClientName().equalsIgnoreCase(username)) {
                occurrence++;
            }
        }
        if ((this.connections.size() >= 1) && occurrence > 1) {
            /*
             * If there is more than one user connected and if the username already exists,
             * then user should use another name.
             * This case excludes the very first registering.
             */
            return true;
        }
        return false;
    }

    /** Checks if a username is already being used when renaming. **/
    public boolean containsForRename(String username) {
        for (MessengerHandler handler : this.connections) {
            if (handler.getClientName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a username is connected to a handler. This also checks if a user
     * with a specific name is present.
     * 
     * @param message
     */
    public boolean containsUsername(String username) {
        for (MessengerHandler handler : this.connections) {
            if (username.equalsIgnoreCase(handler.getClientName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method gets all the usernames that are currently connected to the
     * server.
     * 
     */
    public ArrayList<String> getAllUsernames() {

        ArrayList<String> usernameList = new ArrayList<String>(this.connections.size());
        for (MessengerHandler handler : this.connections) {
            if (handler.getClientName().equalsIgnoreCase(null) == false) {
                usernameList.add(handler.getClientName());
            }
        }
        return usernameList;
    }

    /**
     * This method checks if this name is a valid group name.
     * 
     * @param groupName
     * @return
     */
    public boolean checkGroupName(String groupName) {
        if (getAllGroupNames().contains(groupName)) {
            return true;
        }
        return false;
    }

    /**
     * A method to find a user by a username, and return the message handler.
     * 
     * @param username
     * @return
     */
    public MessengerHandler findUser(String username) {
        for (MessengerHandler messengerHandler : connections) {
            if (messengerHandler.getClientName().equalsIgnoreCase(username)) {
                return messengerHandler;
            }
        }
        return null;
    }

    /**
     * This method sends a specific message to a specific user.
     * 
     * @param username
     * @param message
     * @return
     */
    public boolean sendToUser(String username, Message message) {
        MessengerHandler recipient = findUser(username);
        if (recipient != null) {
            recipient.sendMessageToClient(message);
            return true;
        }
        return false;
    }

    /** This method gets all the group names. */
    public ArrayList<String> getAllGroupNames() {
        Set<String> groupNameSet = this.groupHandler.getGroupNames();
        ArrayList<String> groupList = new ArrayList<String>();
        groupList.addAll(groupNameSet);
        return groupList;
    }

    /**
     * This function returns the occupancy of the groups, and the group name.
     */
    public ArrayList<String> getGroupNameAndOccupancy() {
        ArrayList<String> groupList = new ArrayList<String>();
        for (Group group : this.groupHandler.groupHashMap.values()) {
            groupList.add(
                    String.format("%s \t occupancy[%s]\n", group.groupName, String.valueOf(group.currentOccupancy)));
        }

        return groupList;
    }

    /**
     * Broadcasts a message to all currently connected users, by checking if their
     * name is not null.
     **/
    public void broadcast(Message message) {
        for (MessengerHandler handler : this.connections) {
            String clientName = handler.getClientName();
            if (clientName == null) {
                continue;
            } else if (!clientName.equals(message.getUser())) {
                // System.out.println("Relaying to " + handler.getClientName());
                handler.sendMessageToClient(message);
            }
        }
    }
}