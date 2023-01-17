package MovieTranscripts;

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
            System.out.println("1) Harry Potter e a Pedra Filosofal\n");
            int i=0;
            while ((userInput = systemIn.readLine()) != null) {
                cancelarFilme();
                out.println(userInput);
                out.flush();

                clear();
                String response;
                while(!(response= in.readLine()).equals("endlogo")) {
                    System.out.println(response);
                }
                while (!(response= in.readLine()).equals("end")) {
                    clear();

                    System.out.println(response);
                    int k;
                    for(k=0;k<5;k++) System.out.println("\n");
                    k=0;

                }
            }
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clear(){
        int j;
        for(j=0;j<55;j++){
            System.out.println("\n");
        }
    }

    public static boolean cancelarFilme(){
        Thread thread = new Thread(()->{
            BufferedReader systemIn = new BufferedReader(new InputStreamReader(System.in));
            try {
                if(systemIn.readLine().equalsIgnoreCase("q"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }
}
