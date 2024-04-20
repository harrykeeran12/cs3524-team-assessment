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
  private ArrayList<MessengerHandler> messengerHandlerList = new ArrayList<MessengerHandler>();
  public Integer currentOccupancy = 0;

  /**
   * Constructs a new group with the specified group name
   * @param newGroupName
   */
  public Group(String newGroupName) {
    this.groupName = newGroupName;
  }

  /**
   * Enables a user to join a group chat.
   * @param userMessageHandler
   */
  public void joinGroup(MessengerHandler userMessageHandler) {
    /*
     * If the username is not in the array list of message handlers, add them to the
     * group.
     */
    if (messengerHandlerList.contains(userMessageHandler) == false) {
      messengerHandlerList.add(userMessageHandler);
    }
    this.currentOccupancy += 1;
  }

  /**
   * Enables a user to leave a group chat
   * @param userMessageHandler
   */
  public void leaveGroup(MessengerHandler userMessageHandler) {
    /*
     * If the username is in the array list of message handlers, then remove them
     * from the group.
     */
    if (messengerHandlerList.contains(userMessageHandler) == true) {
      messengerHandlerList.remove(userMessageHandler);
      /* Reduce the current number inside the Group object. */
      if (this.currentOccupancy > 0) {
        this.currentOccupancy -= 1;
      }
    }
  }

  /**
   * Checks the membership of a username in a group.
   * @param username
   * @return
   */
  public boolean checkMembership(String username) {
    for (MessengerHandler messengerHandler : messengerHandlerList) {
      if (messengerHandler.getClientName().equalsIgnoreCase(username)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Broadcasts a message to all currently connected users who are part
   * of the same group chat, by checking if their name is not null.
   * @param newMessage
   */
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