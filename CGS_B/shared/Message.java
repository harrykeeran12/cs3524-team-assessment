package shared;

import java.io.Serializable;

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

    @Override
    public String toString(){
        return String.format("%s : %s", this.user, this.messageBody);
    }
}
