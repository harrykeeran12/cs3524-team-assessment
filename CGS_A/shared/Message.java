package shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// all the data is stored here and shared across server and clients
public class Message implements Serializable {
    private String messageBody;
    private String user;

    public Message(String messageBody, String user) {
        this.messageBody = messageBody;
        this.user = user;
    }

    public Message(String messageBody){
        this.messageBody = messageBody;
        this.user = "[SERVER]";
    }

    public String getMessageBody(){
        return this.messageBody;
    }

    public String getUser(){
        return this.user;
    }
    /**
     * Check the message body to see if there are any topics inside.
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

    @Override
    public String toString(){
        return String.format("%s : %s", this.user, this.messageBody);
    }
}
