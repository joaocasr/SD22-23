package sd20200915;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String [] args) throws IOException {
        ServerSocket serverSocket= new ServerSocket(4444);
        Atendimento a = new Atendimento();
        System.out.println("gekko");
        while (true){
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new ServerWorker(socket,a));
            thread.start();
        }
    }
}