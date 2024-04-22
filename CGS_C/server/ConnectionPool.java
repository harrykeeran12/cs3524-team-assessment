package server;

import java.util.ArrayList;
import java.util.List;

import shared.Message;

/** This class houses all the current connections of the server. **/

public class ConnectionPool {
    private List<MessengerHandler> connections = new ArrayList<>();

    /**
     * Adds a new connection
     * @param handler
     */
    public void addConnection(MessengerHandler handler) {
        System.out.println("\t [SERVER]: Added a new connection to the handler.");
        connections.add(handler);
    }

    /**
     * Removes a user from the connection pool.
     * @param handler
     */
    public void removeUser(MessengerHandler handler) {
        System.out.println("\t [SERVER]: Removed a connection from the handler.");
        connections.remove(handler);
    }

    /**
     * Checks if a username is already being used. This also checks if a user
     * with a specific name is present.
     * @param username
     * @return
     */
    public boolean containsUsername(String username){
        for (MessengerHandler handler : this.connections) {
            if (username.equalsIgnoreCase(handler.getClientName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Broadcasts a message to all currently connected users, by checking if their
     * name is not null.
     * @param message
     */
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