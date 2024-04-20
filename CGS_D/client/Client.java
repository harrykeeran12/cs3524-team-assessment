package CGS_D.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This is the client class, which takes a string, the host and the port, and
 * tries to connect, to send messages.
 **/
public class Client {
    
    private Socket socket;
    private String host;
    private int port;

    /**
     * This is the constructor for the client.
     * @param host
     * @param port
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        this.socket = null;
    }

    /**
     * Returns an output stream from the client to the server
     * @return an ObjectOutputStream connected to the socket's output stream
     * @throws IOException
     */
    private ObjectOutputStream getStreamToMessenger() throws IOException {
        return new ObjectOutputStream(this.socket.getOutputStream());
    }

    /**
     * Returns an input stream from the server to the client
     * @return an ObjectInputStream connected to the socket's input stream
     * @throws IOException
     */
    private ObjectInputStream getStreamFromMessenger() throws IOException {
        return new ObjectInputStream(this.socket.getInputStream());
    }

    /**
     * Returns the client's message from the terminal
     * @return String 
     */
    public String getMessageFromTerminal() {
        Scanner input = new Scanner(System.in);
        System.out.println("Write your message here: ");
        String message = input.nextLine();
        input.close();
        return message;
    }

    /**
     * Connects the client to the server by creating a socket with host and port number
     * @throws IOException
     * @throws UnknownHostException
     */
    public void connect() throws IOException, UnknownHostException{
        this.socket = new Socket(this.host, this.port);
    }


    /**
     * This is supposed to run the client
     * @throws IOException
     * @throws UnknownHostException
     */
    public void run() throws IOException, UnknownHostException{
        Scanner input = new Scanner(System.in);
        try {
            ObjectOutputStream outputStreamToMessenger = this.getStreamToMessenger();
            ObjectInputStream inputStreamFromMessenger = this.getStreamFromMessenger();
            while(true){
                System.out.println("Write your message here: ");
                String message = input.nextLine();
                outputStreamToMessenger.writeObject(message);
                if (message.equalsIgnoreCase("exit")){
                    System.out.println("Client disconnecting...");
                    input.close();
                    socket.close();
                    break;
                }
                    String echoMessenger = (String) inputStreamFromMessenger.readObject();
                    System.out.println(echoMessenger);
                } 
        }catch(ClassNotFoundException e) {
            System.out.println("Received unsupported object from Messenger");
        } catch (NoSuchElementException e) {
            // This might be thrown by 'input.nextLine()' if the client exits with CTRL-C.
            System.out.println("Client disconnecting...");
            input.close();
            socket.close();
        }
    }
}
