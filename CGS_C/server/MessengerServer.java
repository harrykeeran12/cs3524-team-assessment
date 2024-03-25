package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

 import shared.Message;

/** This class houses the port, server socket, and connection pool. **/
public class MessengerServer {

    private int port;
    private ServerSocket serverSocket;
    private ConnectionPool connectionPool;

    /** Constructor to add the items to the socket. **/
    public MessengerServer(int port) {
        this.port = port;
        // this.serverSocket = null;
        // this.connectionPool = null;
    }

    /** This returns a new input stream. **/
    private ObjectInputStream getStreamFromClient(Socket socket) throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }
    /** This returns a new output stream. **/
    private ObjectOutputStream getStreamToClient(Socket socket) throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }


    /** This creates a new server socket. */
    public void connect() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
    }

    /** This is a setup method, that sets up the server. */
    private void setup() throws IOException {
        System.out.println("Setting up messenger server...");
        // this.serverSocket = new ServerSocket(this.port);
        // this.connect();
        // make a connection pool for all the connecting clients.
        this.connectionPool = new ConnectionPool();
        System.out.println("Setup complete!");

    }

    /** This waits for the client connection. */
    private MessengerHandler awaitClientConnection() {
        System.out.println("Waiting for new client connection...");
        try {
            /* Checks if a client is connected. */
            Socket socket = this.serverSocket.accept();
            System.out.println("New client connected.");
            /* In reality maybe send a callback to the client. */

            /* This creates a handler, to handle any messages. */
            MessengerHandler handler = new MessengerHandler(
                    socket,
                    this.connectionPool);
            this.connectionPool.addConnection(handler);
            return handler;

        } catch (IOException e) {
            // e.printStackTrace();
            System.err.println("Could not establish connection with client.");
            return null;
        }
    }

    /**
     * This method handles the client connection, and runs the handler on a separate
     * thread.
     **/
    private void start() {
        while (true) {
            MessengerHandler handler = this.awaitClientConnection();
            if (handler != null) {
                // Start chat listener thread, to listen to the chat.
                Thread chatThread = new Thread(handler);
                /* Starts the handler's thread. */
                chatThread.start();
                System.out.println("This while true is being blocking.");
            } else {
                // If a client failed connecting stop the server.
                System.out.println("Client down.");
                break;
            }
        }
    }

    /** This is supposed to run the server.*/
    public void run() {

        System.out.println("Server is starting...");

        try {
            this.setup();
            this.start();
            /* The server socket should be able to send something back to the client, */
            

            // System.out.println("The run method is running in the MessengerServer.");

            /* The keywords. */
            // String[] keywords = { "REGISTER", "LOGIN", "LOGOUT" };

            // while (true) {
            // String msgFromClient = (String) inputStream.readObject();
            // System.out.println("Client sent a message: " + msgFromClient);
            // for (String keyword : keywords) {
            // /* Check every keyword. */
            // if (msgFromClient.split(" ")[0].equalsIgnoreCase(keyword)) {
            // /* This checks if the first part of the message contains a keyword. */
            // if(keyword.equals("REGISTER")) {}//needs to define a Register method
            // else if (keyword.equals("LOGIN")){} //login method
            // else { //keyword=LOGOUT
            // this.connectionPool.removeUser(this);
            // socket.close();
            // this.connectionPool.broadcast(this.getUserMessage("just left the chat"));
            // }
            // System.out.println("Contains a keyword.");
            // System.out.printf("%s \n", keyword);
            // }
            // }

            // outputStream.writeObject("[Your message '" + msgFromClient + "' has been sent
            // with success.]");
            // System.out.println("MessengerServer echoes to the client.");

            // if(msgFromClient.equalsIgnoreCase("exit")){
            // System.out.println("Client disconnecting, Messenger closing...");
            // socket.close();
            // break;
            // }
            // }
        }
        // } catch(IOException | ClassNotFoundException e){
        // System.out.println("Encountered an error during server execution");
        // }
        catch (IOException e) {
            System.out.println("Encountered an error during server execution");
        }
    }
}