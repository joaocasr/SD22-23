package sd20200915;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    public static void main(String [] args) throws IOException {
        try{
            Socket socket = new Socket("localhost",4444);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            System.out.println("1)espera ie:(1;'nome')\n2)desistir ie:(2;'nome')\n3)atende ie:(3;'something')");
            while ((userInput = systemIn.readLine()) != null) {
                // para desistir é necessario abrir outro processo visto que o cliente está programado para enviar o pedido e de seguida ler a resposta do servidor
                out.println(userInput);
                out.flush();

                String response;
                response= in.readLine();
                System.out.println(response);

            }
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
