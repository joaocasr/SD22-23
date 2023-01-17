package sd20220209;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerWorker implements Runnable {

    private Socket socket;
    private Gestao gestao;

    public ServerWorker(Socket socket,Gestao g) {
        this.socket = socket;
        this.gestao=g;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            while ((line = in.readLine()) != null) {
                if(line.contains("ABANDONEI")){
                    int lista = Integer.parseInt(line.split(";")[1]);
                    gestao.abandona(lista);
                }else if(line.contains("STATUS")){
                    int a = gestao.aEspera();
                    int b = gestao.naSala();
                    String resp = "À espera: "+a+"\n Na sala:"+b;
                    out.println(resp);
                } else{
                    int lista = Integer.parseInt(line.split(";")[1]);
                    gestao.participa(lista);
                    out.println("Adicionado com sucesso à reunião da lista "+lista);
                }
                out.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}