import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;

public class RecompensaTest {

    @Test
    public void constructorTest() {
        Recompensa recompensa = new Recompensa("Guimarães", "Braga", 23);
        System.out.println(recompensa.toString());;
    }

    @Test
    public void verTrotinetes() throws FileNotFoundException {
        Mapa m = new Mapa();
        Local[][] locais = m.getMapa();
        int counter = 0;

        for (int i = 0; i < locais.length; i++) {
            for(int j = 0; j < locais[0].length; j++) {
                if (locais[i][j].getAllTrotinetesLivres().size() > 1) {
                    System.out.println(locais[i][j].getName() + " -> " + locais[i][j].getAllTrotinetesLivres().size());
                    counter++;
                }
            }
        }
        System.out.println(counter);
    }

    // Não ligar a este teste, isto foi uma primeira abordagem à geração de recompensas!
    @Test
    public void geraRecompensaTest() throws FileNotFoundException {
        Map<String,Recompensa> recompensas = new HashMap<>();
        Mapa m = new Mapa();
        int D = 1;
        int total = 0;
        int freeTrotinetes = 0;
        Local[][] locals = m.getMapa();
        Map<String,Integer> trotinetesProximidade = new HashMap<>();

        List<Local> prox = m.calculaProximidades(locals[0][0].getName(), 2);
        System.out.println(locals[0][0].getName());

        for (int i = 0; i < locals.length; i++) {
            for (int j = 0; j < locals.length; j++) {
                freeTrotinetes = locals[i][j].getAllTrotinetesLivres().size();
                // TODO: Avisar que o ficheiro com os locais tem 8 locais repetidos!
                //if (trotinetesProximidade.containsKey(locals[i][j].getName()))
                //   System.out.println(locals[i][j].getName());
                trotinetesProximidade.put(locals[i][j].getName(),freeTrotinetes);
                total += freeTrotinetes;
            }
        }

        //Transfer as List and sort it
        ArrayList<Map.Entry<String, Integer>> l = new ArrayList(trotinetesProximidade.entrySet());
        Collections.sort(l, new Comparator<Map.Entry<?, Integer>>(){

            public int compare(Map.Entry<? , Integer> o1, Map.Entry<?, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        System.out.println(l);
        System.out.println(l.size());
        System.out.println(l.get(l.size()-1));
        int aux = 1;
        int coords;
        int trotinetes = 0;
        int x =0, y = 0;
        List<Local> surroundings;

        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).getValue() > 1) {
                if (l.get(l.size()-aux).getValue() == 0) {
                    coords = m.getCoords(l.get(l.size() - aux).getKey());
                    x = coords / 10;
                    y = coords % 10;
                    surroundings = m.getSurrounding(x, y, D);
                    System.out.println("X: " + x + " Y: " + y);
                    for (Local local1 : surroundings) {
                        trotinetes += local1.getAllTrotinetesLivres().size();
                    }
                    if (trotinetes == 0)
                        recompensas.put(l.get(i).getKey(), new Recompensa(l.get(i).getKey(), l.get(l.size()-aux).getKey(), m.calculaDistancia(l.get(i).getKey(), l.get(l.size()-aux).getKey())));
                    else i--;
                    aux++;
                    trotinetes = 0;
                }
                else break;
            }
            else break;
        }

        System.out.println(recompensas);
        System.out.println(recompensas.size());
        System.out.println(total);
    }

    @Test
    public void recompensaTest() throws FileNotFoundException {
        Mapa m = new Mapa();
        m.initTrotLivres();
        m.geraRecompensas();
        System.out.println(m.getRecompensas());
    }

    @Test
    public void recompensasProxTest() throws FileNotFoundException {
        Mapa m = new Mapa();
        m.initTrotLivres();
        m.geraRecompensas();
        //System.out.println(m.getRecompensas());
        String origem = "Rua Albino Duarte Pinheiro";
        int dist = 5;
        System.out.println(m.getRecompensaProximidade(origem, dist));
    }
}
