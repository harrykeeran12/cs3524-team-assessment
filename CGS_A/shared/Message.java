package shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the structure of users' messages. All the data is stored here and shared across server and clients
 */
public class Message implements Serializable {
    private String messageBody;
    private String user;

    /**
     * Constructs a new message with the message body and the sender who can be either a user or the server
     * @param messageBody
     * @param user
     */
    public Message(String messageBody, String user) {
        this.messageBody = messageBody;
        this.user = user;
    }

    /**
     * Constructs a new message with the message body and the server
     * @param messageBody
     */
    public Message(String messageBody){
        this.messageBody = messageBody;
        this.user = "[SERVER]";
    }

    /**
     * Gets the message body of the sender's message
     * @return
     */
    public String getMessageBody(){
        return this.messageBody;
    }

    /**
     * Gets the sender of a message
     * @return
     */
    public String getUser(){
        return this.user;
    }

    /**
     * Checks the message body to see if there are any topics inside.
     * @param topicList
     * @return
     * @throws Exception
     */
    public Set<String> checkMessageBody(Set<String> topicList) throws Exception{
        Set<String> containedTopics = new HashSet<String>();
        for (String topic : topicList) {
            if (this.messageBody.contains(topic)) {
                containedTopics.add(topic);
                
            }
        }
        return containedTopics;
    }

    /**
     * Overwrites the toString() method. Converts a Message into a String.
     */
    @Override
    public String toString(){
        return String.format("%s : %s", this.user, this.messageBody);
    }
}
