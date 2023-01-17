package MovieTranscripts;

import sd20190726.Reune;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ServerWorker implements Runnable{
    private Socket socket;

    public ServerWorker(Socket s){
        this.socket=s;
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            boolean b=false;
            while ((line = in.readLine()) != null) {
                if(Integer.parseInt(line)==1){
                    System.out.println("comeco");
                    List<String> l = lerFicheiro("/home/joao/IdeaProjects/SD-testes/src/main/java/MovieTranscripts/harry-potter-and-the-sorcerers-stone-yify-english.srt");
                    if(l.size()==0) System.out.println("vazio");
                    int i=0;
                    for(String s: l){
                        out.println(s);
                        out.flush();
                        i++;
                        if(i<20 || s.isEmpty()) Thread.sleep(500);
                        if(i==20){
                            out.println("endlogo");
                            out.flush();
                        }
                        if(i>20) Thread.sleep(7000);
                    }
                    out.println("end");
                    out.flush();
                }
            }
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<String> lerFicheiro (String nomeFich)  {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
        } catch (IOException exc) {
            lines = new ArrayList<>();
        }
        return lines;
    }
}
