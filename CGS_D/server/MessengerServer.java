package CGS_D.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

/** This class houses the port, server socket, and connection pool. **/
public class MessengerServer{

    private int port;
    private ServerSocket serverSocket;

    /**
     * Constructor to add the items to the socket.
     * @param port
     */
    public MessengerServer(int port){
        this.port = port;
        this.serverSocket = null;
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
     * This returns a new input stream.
     * @param socket
     * @return
     * @throws IOException
     */
    private ObjectInputStream getStreamFromClient(Socket socket) throws IOException{
        return new ObjectInputStream(socket.getInputStream());
    }

    /**
     * This creates a new server socket.
     * @throws IOException
     */
    public void connect() throws IOException{
        this.serverSocket = new ServerSocket(this.port);
    }

    /** This is supposed to run the server. */
    public void run() {
        System.out.println("Server is starting...");

        try {
            Socket socket = this.serverSocket.accept();
            System.out.println("Client connected.");
            ObjectInputStream inputStream = this.getStreamFromClient(socket);
            ObjectOutputStream outputStream = this.getStreamToClient(socket);
            while(true){
                String msgFromClient = (String) inputStream.readObject();
                System.out.println("Client sent a message: " + msgFromClient);
                outputStream.writeObject("[Your message '" + msgFromClient + "' has been sent with success.]");
                System.out.println("MessengerServer echoes to the client.");

                if(msgFromClient.equalsIgnoreCase("exit")){
                    System.out.println("Client disconnecting, Messenger closing...");
                    socket.close();
                    break;
                }
            }
        } catch(IOException | ClassNotFoundException e){
            System.out.println("Encontered an error during server execution");
        }
    }
}