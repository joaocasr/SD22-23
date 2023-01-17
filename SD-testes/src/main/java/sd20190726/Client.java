package sd20190726;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String [] args) throws IOException {
        try{
            Socket socket = new Socket("localhost",4444);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            System.out.println("Digite o seu idioma:\nDIGITE 'G'(PARA GUIAR UM GRUPO) ");
            while ((userInput = systemIn.readLine()) != null) {
                out.println(userInput);
                out.flush();

                String response;
                while (!(response= in.readLine()).equals("end")) {
                    System.out.println(response);
                }
            }
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
