package server;

import java.util.HashMap;
import java.util.Set;

/**
 * GroupHandler:
 * The group handler class contains methods for managing a group.
 * A group is a list of users.
 * This could also be written as a type of ConnectionPool.
 */
public class GroupHandler {

  protected HashMap<String, Group> groupHashMap = new HashMap<String, Group>();

  /**
   * Allow a user to create a new group.
   * 
   * @param groupName
   * @return a new Group.
   * @throws Exception
   */
  public Group createGroup(String groupName) throws Exception {
    /* If there is not already a groupName with a specific string, create one. */
    if (groupHashMap.containsKey(groupName) == false) {
      Group newGroup = new Group(groupName);
      groupHashMap.put(groupName, newGroup);
      System.out.printf("Created new group with name %s \n", groupName);
      return newGroup;
    } else {
      throw new Exception("Group already exists with that name.");
    }
  }

  /**
   * Allow a user to join a specific group.
   * 
   * @param groupName
   * @param userMessengerHandler
   * @return a specific group specified by the user.
   * @throws Exception
   */
  public Group joinGroup(String groupName, MessengerHandler userMessengerHandler) throws Exception {

    if (groupHashMap.containsKey(groupName) && userMessengerHandler != null) {
      Group newGroupToBeJoined = groupHashMap.get(groupName);
      newGroupToBeJoined.joinGroup(userMessengerHandler);
      return newGroupToBeJoined;

    } else {
      throw new Exception("Specified group cannot be found.");
    }

  }

  /**
   * Allow a user(represented by a handler, to leave a specific group).
   * 
   * @param groupName
   * @param userMessengerHandler
   * @throws Exception
   */
  public void leaveGroup(String groupName, MessengerHandler userMessengerHandler) throws Exception {
    if (groupHashMap.containsKey(groupName)) {
      Group groupToLeave = groupHashMap.get(groupName);
      groupToLeave.leaveGroup(userMessengerHandler);

    } else {
      throw new Exception("Specified group cannot be found.");
    }
  }

  /**
   * Remove a specific group from the group hash map.
   * 
   * @param groupName
   * @throws Exception
   */
  public void removeGroup(String groupName) throws Exception {
    if (groupHashMap.containsKey(groupName)) {
      groupHashMap.remove(groupName);

    } else {
      throw new Exception("Group was unable to be removed.");
    }
  }
/**
 * This finds a group. 
 * @param groupName
 * @return
 * @throws Exception
 */
  public Group findGroup(String groupName) throws Exception{
    if (groupHashMap.containsKey(groupName)) {
      Group newGroupToBeJoined = groupHashMap.get(groupName);
      return newGroupToBeJoined;

    } else {
      throw new Exception("Specified group cannot be found.");
    }
  }
  /**
   * This function returns all the names of the groups that have currently been
   * created.
   */
  public Set<String> getGroupNames() {
    return groupHashMap.keySet();
  }

 
}