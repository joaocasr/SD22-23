package sd20210721;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String [] args) throws IOException {
        Socket socket = new Socket("localhost",4444);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        System.out.println("1)vacinar \n2)abastecer");
        String input;
        while((input=in.readLine())!=null){
            out.println(Integer.parseInt(input));
            System.out.println(in.readLine());
        }

        socket.close();
    }
}
