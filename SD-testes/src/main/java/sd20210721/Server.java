package sd20210721;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String [] args) throws IOException {
        ServerSocket serverSocket= new ServerSocket(4444);
        Gestao gestao = new Gestao();
        while (true){
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new ServerWorker(socket,gestao));
            thread.start();
        }
    }
}
