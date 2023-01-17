package sd20220209;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

public class Cliente {
    private static ReentrantLock lock = new ReentrantLock();
    private static Map<Integer,String> m = new HashMap<>();
    public static void main(String [] args) throws IOException {
        try{
            Socket socket = new Socket("localhost",4444);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            System.out.println("Digite a operacao seguido do n da lista com um ; ");
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                String response = in.readLine();
                System.out.println(response);
            }
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}