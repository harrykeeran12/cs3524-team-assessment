package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class MessengerServer{
    

    private int port;
    private ServerSocket serverSocket;

    public MessengerServer(int port){
        this.port = port;
        this.serverSocket = null;
    }
    /* This returns a new output stream.  */
    private ObjectOutputStream getStreamToClient(Socket socket) throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }
    
    /* This returns a new input stream. */
    private ObjectInputStream getStreamFromClient(Socket socket) throws IOException{
        return new ObjectInputStream(socket.getInputStream());
    }
    /* This connects to the server socket.  */
    public void connect() throws IOException{
        this.serverSocket = new ServerSocket(this.port);
    }

    public void run() {
        System.out.println("Server is starting...");

        try {
            Socket socket = this.serverSocket.accept();
            System.out.println("Client connected.");
            ObjectInputStream inputStream = this.getStreamFromClient(socket);
            ObjectOutputStream outputStream = this.getStreamToClient(socket);
            /* The keywords. */
            String[] keywords = {"REGISTER", "LOGIN", "LOGOUT"};

            while(true){
                String msgFromClient = (String) inputStream.readObject();
                System.out.println("Client sent a message: " + msgFromClient);
                for (String keyword : keywords) {
                    /* Check every keyword. */
                    if (msgFromClient.split(" ")[0].equalsIgnoreCase(keyword)) {
                        /* This checks if the first part of the message contains a keyword. */
                        System.out.println("Contains a keyword.");
                        System.out.printf("%s \n", keyword);
                    }
                }
                outputStream.writeObject("[Your message '" + msgFromClient + "' has been sent with success.]");
                System.out.println("MessengerServer echoes to the client.");

                if(msgFromClient.equalsIgnoreCase("exit")){
                    System.out.println("Client disconnecting, Messenger closing...");
                    socket.close();
                    break;
                }
            }
        } catch(IOException | ClassNotFoundException e){
            System.out.println("Encountered an error during server execution");
        }
    }
}