package server;

import java.util.ArrayList;
import java.util.List;

import shared.Message;
import server.GroupHandler;

/** This class houses all the current connections of the server. **/

public class ConnectionPool {
    private List<MessengerHandler> connections = new ArrayList<>();
    public GroupHandler groupHandler = new GroupHandler();

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