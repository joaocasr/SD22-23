package sd20190726;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerWorker implements Runnable{
    private Socket socket;
    private Reune reune;
    private List<String> dialogoPT;
    private List<String> dialogoEN;

    public ServerWorker(Socket s,Reune r){
        this.socket=s;
        this.reune=r;
        this.dialogoPT=new ArrayList<>();
        this.dialogoEN=new ArrayList<>();
    }
    @Override
    public void run() {
        auxaPT();
        auxEN();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String line;
            int r=0;
            boolean b=false;
            while ((line = in.readLine()) != null) {
                if(line.contains("PT")){
                    System.out.println("chegou pt");
                    b=reune.enterPT();
                    r=1;
                }else if(line.equalsIgnoreCase("G")){
                    reune.enterGuide();
                    r=2;
                }
                if(r==1 && b==true){
                    System.out.println("comeco");
                    for(String s: dialogoPT){
                        out.println(s);
                        out.flush();
                        Thread.sleep(4000);
                    }
                    out.println("end");
                    out.flush();
                }
                r=0;
            }
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void auxaPT(){
        this.dialogoPT.add("Japão é um país formado por um conjunto de ilhas situadas na porção oriental do continente asiático. A geografia local é diversa e marcada pela instabilidade geológica. Além disso, a história japonesa é milenar e heterogênea.\n");
        this.dialogoPT.add("O país, antes uma notável potência imperialista, foi duramente atingido pela Segunda Grande Guerra (1939–1945). A derrota japonesa nesse conflito resultou na adoção de uma política externa mais pacifista.\n");
        this.dialogoPT.add("Atualmente, o Japão é uma grande potência econômica e industrial. O país possui uma infraestrutura moderna e detém elevados indicadores sociais.\n");
        this.dialogoPT.add("A população japonesa desfruta de um alto padrão de vida. A cultura do Japão possui particularidades significativas, com destaque para a realização de diversos rituais, bem como para o respeito entre os indivíduos desse povo.\n");
        this.dialogoPT.add("A história do Japão é complexa e envolve diversos períodos históricos. A Era Meiji é um dos principais marcos da história moderna japonesa.\n");
        this.dialogoPT.add("O Japão foi uma das nações derrotadas na Segunda Guerra Mundial (1939–1945). O país rendeu-se após o ataque com bombas atômicas realizado pelos Estados Unidos\n");
        this.dialogoPT.add("A geografia japonesa é caracterizada pelo relevo montanhoso. O país possui inúmeros vulcões e registra muitos tremores de terra.\n");
        this.dialogoPT.add("O território japonês é altamente populoso e povoado. A capital do Japão, Tóquio, é considerada a maior aglomeração urbana do mundo.\n");
        this.dialogoPT.add("A economia japonesa é a terceira maior do planeta. O país é forte nos setores de serviços diversos e na produção de bens manufaturados\n");
        this.dialogoPT.add("A infraestrutura japonesa é extremamente moderna. O país é pioneiro no desenvolvimento de tecnologias nas áreas de transporte e telecomunicação\n");
        this.dialogoPT.add("O sistema governamental do Japão é composto por uma monarquia constitucional unitária parlamentarista\n");
        this.dialogoPT.add("A cultura japonesa envolve vários aspectos do mundo digital, como os desenhos animados e os jogos de videogame.\n");
        this.dialogoPT.add("Os japoneses possuem hábitos culturais bastante específicos que são encarados como exóticos por grande parte da população mundial.\n");
        this.dialogoPT.add("Chegamos ao final da visita. Espero que tenham gostado.\n");
    }

    public void auxEN(){
        this.dialogoEN.add("Japan is a country formed by a group of islands located in the eastern portion of the Asian continent. The local geography is diverse and marked by geological instability. Furthermore, Japanese history is millenary and heterogeneous.\n");
        this.dialogoEN.add("The country, once a notable imperialist power, was hard hit by the Second World War (1939–1945. The Japanese defeat in that conflict resulted in the adoption of a more pacifist foreign policy.\n");
        this.dialogoEN.add("Currently, Japan is a great economic and industrial power. The country has a modern infrastructure and has high social indicators.\n");
        this.dialogoEN.add("The Japanese population enjoys a high standard of living. The culture of Japan has significant particularities, with emphasis on the performance of various rituals, as well as the respect between individuals of this people.\n");
        this.dialogoEN.add("The history of Japan is complex and involves several historical periods. The Meiji Era is one of the main landmarks of modern Japanese history.\n");
        this.dialogoEN.add("Japan was one of the defeated nations in World War II (1939–1945). The country surrendered after the atomic bomb attack carried out by the United States\n");
        this.dialogoEN.add("Japanese geography is characterized by mountainous relief. The country has numerous volcanoes and records many earthquakes.\n");
        this.dialogoEN.add("The Japanese territory is highly populous and populated. The capital of Japan, Tokyo, is considered the largest urban agglomeration in the world.\n");
        this.dialogoEN.add("The Japanese economy is the third largest on the planet. The country is strong in the various service sectors and in the production of manufactured goods\n");
        this.dialogoEN.add("The Japanese infrastructure is extremely modern. The country is a pioneer in the development of technologies in the areas of transport and telecommunications\n");
        this.dialogoEN.add("Japan's governmental system consists of a parliamentary unitary constitutional monarchy\n");
        this.dialogoEN.add("Japanese culture involves many aspects of the digital world, such as cartoons and video games.\n");
        this.dialogoEN.add("The Japanese have very specific cultural habits that are seen as exotic by a large part of the world's population.\n");
        this.dialogoEN.add("We have reached the end of the visit. I hope you enjoyed it.");
    }
}
