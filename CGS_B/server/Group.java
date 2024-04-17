package server;

import java.util.ArrayList;

import shared.Message;

/**
 * Group:
 * A list of users who can all communicate with each other separately.
 * This group will have a specific broadcast function to notify each member in
 * the group. Members can add themselves to a group.
 */
public class Group {
  public String groupName;
  private ArrayList<MessengerHandler> messengerHandlerList;
  public Integer currentOccupancy = 0;

  public Group(String newGroupName) {
    this.groupName = newGroupName;
  }

  public void joinGroup(MessengerHandler userMessageHandler) {
    /*
     * If the username is not in the array list of message handlers, add them to the
     * group.
     */
    if (!messengerHandlerList.contains(userMessageHandler)) {
      messengerHandlerList.add(userMessageHandler);
    }
    this.currentOccupancy += 1;
  }

  public void leaveGroup(MessengerHandler userMessageHandler) {
    /*
     * If the username is in the array list of message handlers, then remove them
     * from the group.
     */
    if (messengerHandlerList.contains(userMessageHandler)) {
      messengerHandlerList.remove(userMessageHandler);
      /* Reduce the current number inside the Group object. */
      if (this.currentOccupancy > 0) {
        this.currentOccupancy -= 1;
      }
    }
  }

  public void broadcastGroup(Message newMessage) {
    for (MessengerHandler handler : this.messengerHandlerList) {
      String clientName = handler.getClientName();
      if (clientName == null) {
        continue;
      } else if (!clientName.equals(newMessage.getUser())) {
        // System.out.println("Relaying to " + handler.getClientName());
        handler.sendMessageToClient(newMessage);
      }
    }
  }
}