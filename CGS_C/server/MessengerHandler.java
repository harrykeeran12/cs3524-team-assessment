package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import shared.Message;

public class MessengerHandler implements Runnable{

    private Socket socket;
    private ObjectOutputStream streamToClient;
    private ObjectInputStream streamFromClient;
    private String username;
    private ConnectionPool connectionPool;

    public MessengerHandler (Socket socket, ConnectionPool connectionPool){
        this.socket = socket;
        this.connectionPool = connectionPool;

        try {
            this.streamFromClient = new ObjectInputStream(socket.getInputStream());
            this.streamToClient = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e){
            System.out.println("I/O error");
        }
    }

    public void registerUser() throws IOException, ClassNotFoundException {
        try {
            this.username = (String) this.streamFromClient.readObject();
        } catch (IOException e) {
            System.out.println("User " + this.username + "failed to registered");
        }
        this.connectionPool.broadcast(this.getUserMessage("User "+ this.username + "joined the chat"));
    }

    public String getClientName() {
        return this.username;
    }

    public Message getUserMessage (String messageBody){
        return new Message(messageBody, this.username);
    }

    private void close(){
        this.connectionPool.removeUser(this);
        try {
            this.socket.close();
        } catch (IOException | NullPointerException e) {
            // There was an I/O exception or the socket was not instantiated
            // In either case, do nothing.
        } finally {
            this.connectionPool.broadcast(
                this.getUserMessage("just left the chat.")
            );
        }
    }

    public void sendMessageToClient(Message message){
        try {
            streamToClient.writeObject(message);
        } catch (IOException e) {
            System.out.println("Failed sending the message " + message.getMessageBody() + " to " + this.username);
        }
    }

    @Override
    public void run(){
        try {
            String[] keywords = {"REGISTER", "LOGIN", "LOGOUT"};
            this.registerUser();

            while (true) {
                Message message = (Message) streamFromClient.readObject();
                String messageBody = message.getMessageBody();
                // Overwrite this.username with the one contained in the message
                this.username = message.getUser();
                System.out.println(message.toString());
                for (String keyword : keywords) {
                    /* Check every keyword. */
                    if (messageBody.split(" ")[0].equalsIgnoreCase(keyword)) {
                        /* This checks if the first part of the message contains a keyword. */
                        if(keyword.equals("REGISTER")) //needs to define a Register method
                        else if (keyword.equals("LOGIN")) //login method
                        else { //keyword=LOGOUT
                            this.connectionPool.removeUser(this);
                            socket.close();
                            this.connectionPool.broadcast(this.getUserMessage("just left the chat"));
                        }
                        System.out.println("Contains a keyword.");
                        System.out.printf("%s \n", keyword);
                    }
                }

                if (messageBody.equalsIgnoreCase("exit")) break;
                // send message to all other clients
                connectionPool.broadcast(message);
            }
        } catch (IOException e){
            System.out.println("Communication interrupted with " + this.username);
        } catch (ClassNotFoundException e) {
            System.out.println("Communication interrupted with " + this.username);
        } finally {
            this.close();
        }
    }
}
