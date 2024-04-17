package shared;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * ListenerThread: This thread checks to see if a client is sent to a stream, and printed.
 */
public class ListenerThread extends Thread{

  private ObjectInputStream inputStream;
  private boolean connectionStatus;

  public ListenerThread(ObjectInputStream newInputStream, boolean connected){
    this.inputStream = newInputStream;
    this.connectionStatus = connected;
    // System.out.println("Created a new listener thread.");
  }
  @Override
  public void run() {

    while (true) {
            try {
                Message receivedMessage = (Message) this.inputStream.readObject();
                System.out.println(receivedMessage.toString());
            } catch (ClassNotFoundException e) {
                System.err.println("Could not deserialise the message.");
            } catch (IOException e) {
                if (connectionStatus == false) {
                    // If the program is not exited continue listening
                    System.err.println("Failed while listening to server.");
                } else {
                    // Otherwise, stop listening
                    break;
                }
            }
        }
  }

  
}