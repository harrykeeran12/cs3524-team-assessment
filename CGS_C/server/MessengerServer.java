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

    /**
     * Constructor to add the items to the socket.
     * @param port
     */
    public MessengerServer(int port) {
        this.port = port;
    }

     /**
     * This returns a new input stream.
     * @param socket
     * @return
     * @throws IOException
     */
    private ObjectInputStream getStreamFromClient(Socket socket) throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    /**
     * This returns a new output stream.
     * @param socket
     * @return
     * @throws IOException
     */
    private ObjectOutputStream getStreamToClient(Socket socket) throws IOException {
        return new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * This creates a new server socket.
     * @throws IOException
     */
    public void connect() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
    }

    /**
     * This is a setup method, that sets up the server.
     * @throws IOException
     */
    private void setup() throws IOException {
        System.out.println("Setting up messenger server...");
        // make a connection pool for all the connecting clients.
        this.connectionPool = new ConnectionPool();
        System.out.println("Setup complete!");

    }

    /**
     * This waits for the client connection.
     * @return
     */
    private MessengerHandler awaitClientConnection() {
        System.out.println("Waiting for new client connection...");
        try {
            /* Checks if a client is connected. */
            Socket socket = this.serverSocket.accept();
            System.out.println("Client connection created.");
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
        } catch (NullPointerException e) {
            System.err.println("Server already running on the machine.");
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
            } else {
                // If a client failed connecting, stop the server.
                System.out.println("Client down.");
                break;
            }
        }
    }

    /** This is supposed to run the server. */
    public void run() {

        System.out.println("Server is starting...");

        try {
            this.setup();
            this.start();
        } catch (IOException e) {
            System.out.println("Encountered an error during server execution");
        }
    }
}