package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class MessengerHandler implements Runnable{

    private Socket socket;
    private ObjectOutputStream streamFromClient;
    private ObjectInputStream streamToClient;
    private String username;

    public void registerUser() throws IOException, ClassNotFoundException {

    }

    public String getClientName() {
        return this.username;
    }

    public String getUserMessage (String message){
        String msgFromClient = (String) streamFromClient.readObject();
    }

    @Override
    public run(){}
}
