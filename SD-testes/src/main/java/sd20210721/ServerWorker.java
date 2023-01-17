package sd20210721;

import java.io.*;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private Socket socket;
    private Gestao gestao;
    public ServerWorker(Socket s,Gestao g){
        this.socket=s;
        this.gestao=g;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            while((line=in.readLine())!=null){
                if(Integer.parseInt(line.split(";")[0])==1){
                    gestao.pedirParaVacinar();
                }else if(Integer.parseInt(line.split(";")[0])==2){
                    gestao.fornecerFrascos(Integer.parseInt(line.split(";")[1]));
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
