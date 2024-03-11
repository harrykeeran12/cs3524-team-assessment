package CGS_D.server;

import java.net.UnknownHostException;
import java.io.IOException;

public class RunMessenger {
    public static void main(String[] args){
        MessengerServer messenger = new MessengerServer(50000);
        try {
            messenger.connect();
        } catch (IOException e){
            System.out.println("Encountered error during server startup");
        }
        messenger.run();
    }
}