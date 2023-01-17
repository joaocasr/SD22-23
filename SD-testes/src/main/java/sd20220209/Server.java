package sd20220209;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
public class Server{
    public static void main(String [] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(4444);
        Gestao g = new Gestao();
        while (true){
            System.out.println("begin");
            Socket socket = serverSocket.accept();
            System.out.println("alguem conectou");
            Thread thread = new Thread(new ServerWorker(socket,g));
            thread.start();
        }
    }

}
