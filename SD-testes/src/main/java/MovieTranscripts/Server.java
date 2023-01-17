package MovieTranscripts;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String [] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4444);
        while (true){
            System.out.println("Bem-vindo");
            Socket socket = serverSocket.accept();
            System.out.println("Nova conexão estabelecida.");
            Thread thread = new Thread(new ServerWorker(socket));
            thread.start();
        }
    }
}
