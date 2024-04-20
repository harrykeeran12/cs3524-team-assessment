package server;

import java.util.HashMap;
import java.util.Set;
import java.lang.Exception;

/**
 * TopicHandler:
 * Handles the methods for the topics.
 */
public class TopicHandler {

  private HashMap<String, Group> topicList = new HashMap<String, Group>();

  /**
   * Creates a new topic, and adds it to the topic list.
   * 
   * @param topicName
   * @throws Exception
   */
  /**
   * This lets you create a new topic.
   * 
   * @param topicName
   * @throws Exception
   */
  public void createTopic(String topicName) throws Exception {
    if (topicList.containsKey(topicName) == false) {
      System.out.println(String.format("Creating a new topic %s\n", topicName));
      topicList.put(topicName, new Group(topicName));

    } else {
      throw new Exception("This topic is already in the list.");
    }
  }

  /**
   * This method lets you subscribe to a topic by joining the group.
   * 
   * @param topicName
   */
  public void subscribeToTopic(String topicName, MessengerHandler userHandler) throws Exception {
    if (topicList.containsKey(topicName) == true) {
      Group topicGroup = topicList.get(topicName);
      if (topicGroup.checkMembership(userHandler.getClientName()) == false) {
        System.out.printf("%s is joining the topic group %s. \n", userHandler.getClientName(), topicName);
        topicGroup.joinGroup(userHandler);
      }
      else{
        throw new Exception("User is already in the group.");
      }

    } else {
      throw new Exception("This topic is already in the list.");
    }
  }

  /**
   * This method lets you unsubscribe to a topic by leaving the group.
   * 
   * @param topicName
   */
  public void unsubscribeToTopic(String topicName, MessengerHandler userHandler) throws Exception {
    if (topicList.containsKey(topicName) == true) {
      Group topicGroup = topicList.get(topicName);
      if (topicGroup.checkMembership(userHandler.getClientName()) == true) {
        System.out.printf("%s is leaving the topic group %s. \n", userHandler.getClientName(), topicName);
        topicGroup.leaveGroup(userHandler);
      }
      else{
        throw new Exception("User is not in the group.");
      }

    } else {
      throw new Exception("This topic is already in the list.");
    }
  }
  /**
   * Get a topic group via the name.
   * @return
   */
  public Group getTopicGroup(String topicName) throws Exception{
    if (topicList.containsKey(topicName) == true) {
      return topicList.get(topicName);
    }
    else{
      throw new Exception("Topic name was invalid.");
    }
  }
  /**
   * This method returns the names of all the topics.
   * 
   * @return
   */
  public Set<String> topicList() {
    return topicList.keySet();
  }
  

}