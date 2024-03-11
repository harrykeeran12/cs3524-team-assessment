package server;

import java.util.ArrayList;
import java.util.List;

import shared.Message;

public class ConnectionPool {
    private List<MessengerHandler> connections = new ArrayList<>();
    
    public void addConnection(MessengerHandler handler){
        connections.add(handler);
    }

    public void broadcast(Message message){
        for (MessengerHandler handler: this.connections){
            String clientName = handler.getClientName();
            if (clientName == null) {
                continue;
            } else if (!clientName.equals(message.getUser())) {
                System.out.println("Relaying to " + handler.getClientName());
                handler.sendMessageToClient(message);
            }
        }
    }

    public void removeUser(MessengerHandler handler) {
        // remove the user's connection handler from pool
        connections.remove(handler);
    }
}