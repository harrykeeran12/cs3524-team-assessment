package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import shared.Message;

public class MessengerServer{
    

    private int port;
    private ServerSocket serverSocket;
    private ConnectionPool connectionPool;

    public MessengerServer(int port){
        this.port = port;
        this.serverSocket = null;
        this.connectionPool = null;
    }
    /* This returns a new output stream.  */
    // private ObjectOutputStream getStreamToClient(Socket socket) throws IOException {
    //     return new ObjectOutputStream(socket.getOutputStream());
    // }
    
    // /* This returns a new input stream. */
    // private ObjectInputStream getStreamFromClient(Socket socket) throws IOException{
    //     return new ObjectInputStream(socket.getInputStream());
    // }

    /* This connects to the server socket.  */
    public void connect() throws IOException{
        this.serverSocket = new ServerSocket(this.port);
    }

    private void setup() throws IOException {
        System.out.println("MesssServer starting...");
        this.serverSocket = new ServerSocket(this.port);
        // make a connection pool for all the connecting clients.
        this.connectionPool = new ConnectionPool();
        System.out.println("Setup complete!");
        
    }

    private MessengerHandler awaitClientConnection() {
        System.out.println("Waiting for new client connection...");
        try {
            Socket socket = this.serverSocket.accept();
            System.out.println("New client connected.");
            
            // create server_socket_handler and start it.
            MessengerHandler handler = new MessengerHandler(
                socket,
                this.connectionPool
            );
            this.connectionPool.addConnection(handler);
            return handler;

        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("Could not establish connection with client.");
            return null;
        }
    }

    private void start() {
        while (true){
            MessengerHandler handler = this.awaitClientConnection();
            if (handler != null) {
                // Start chat listener thread 
                Thread chatThread = new Thread(handler);
                chatThread.start();
            } else {
                // If a client failed connecting stop the server.
                // You could also do nothing here and just continue listening
                // for new connections.
                break;
            }
        }
    }

    public void run() {
        System.out.println("Server is starting...");
        
        try {
            this.setup();
            this.start();
            System.out.println("Client connected.");
        
            /* The keywords. */
            String[] keywords = {"REGISTER", "LOGIN", "LOGOUT"};

            while(true){
                String msgFromClient = (String) inputStream.readObject();
                System.out.println("Client sent a message: " + msgFromClient);
                for (String keyword : keywords) {
                    /* Check every keyword. */
                    if (msgFromClient.split(" ")[0].equalsIgnoreCase(keyword)) {
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