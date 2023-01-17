package sd20190726;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String [] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4444);
        Reune reune = new Reune();
        System.out.println("hello");
        while (true){
            Socket socket = serverSocket.accept();
            System.out.println("Nova conexão estabelecida.");
            Thread thread = new Thread(new ServerWorker(socket,reune));
            thread.start();
        }
    }
}
