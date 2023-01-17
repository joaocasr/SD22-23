package sd20200915;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerWorker implements Runnable {
    private Socket socket;
    private Atendimento a;
    public ServerWorker(Socket s,Atendimento a){
        this.socket=s;
        this.a = a;
    }


    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            String nome="";
            boolean b=false;
            int r=0;
            while((line=in.readLine())!=null){
                if(Integer.parseInt(line.split(";")[0])==1){
                    b= a.espera(line.split(";")[1]);
                    r=1;
                }else if(Integer.parseInt(line.split(";")[0])==2){
                    a.desiste(line.split(";")[1]);
                    r=2;
                }else if(Integer.parseInt(line.split(";")[0])==3){
                    nome = a.atende();
                    r=3;
                }
                if(r==1 && b==true) out.println("Você foi atentido com sucesso.");
                if(r==1 && b==false) out.println("Vôce desistiu.");
                if(r==2) out.println("Desistẽncia efetuada com sucesso.");
                if(r==3) out.println("0 cliente "+nome+ " acabou de sair da sala de espera.");
                out.flush();
                r=0;
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}