import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa {
    private Local [][] mapa;
    private int size;
    private List<String> nomes = new ArrayList<>();
    private List<Trotinete> trotinetes = new ArrayList<>();
    private ReentrantLock l = new ReentrantLock();
    private Map<String,Recompensa> recompensas = new HashMap<>();
    private ArrayList<Map.Entry<String, Integer>> trotLivres;

    public Mapa() throws FileNotFoundException {
        int N = this.size = 20;
        mapa = new Local[N][N];
        this.nomes = lerFicheiro("/home/shivverz/Documents/SD/src/main/java/locais");
        int k=0;
        int l;
        for(l=0;l<200;l++){
            String code= "C0X"+l;
            Trotinete t = new Trotinete(code,true);
            trotinetes.add(t);
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                mapa[i][j] = new Local(nomes.get(k),i,j);
                k++;
            }
        }
        for(Trotinete trotinete:trotinetes){
            distribuicaoAleatoria(trotinete);
        }
    }

    public void distribuicaoAleatoria(Trotinete t){
        int x = (int)(Math.random()*(20));
        int y = (int)(Math.random()*(20));
        mapa[x][y].adicionaTrotinete(t);
    }


    public Local getLocal(int x,int y){
        try{
            l.lock();
            return this.mapa[x][y];
        }finally {
            l.unlock();
        }
    }

    public ArrayList<Map.Entry<String, Integer>> getTrotLivres() {
        return this.trotLivres;
    }

    public Map<String,Recompensa> getRecompensas() {
        return recompensas;
    }

    public int getCoords(String name) throws RuntimeException {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (name.equals(this.mapa[i][j].getName())) {
                    return i*10 + j;
                }
            }
        }

        throw new RuntimeException();
    }

    public List<Local> getSurrounding(int x, int y, int dist) {
        List<Local> s = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if ( (x - dist <= i && i <= x + dist) && (y - dist <= j && j <= y + dist) && x != i && y != j) {
                    s.add(mapa[i][j]);
                }
            }
        }

        return s;
    }

    public void initTrotLivres() {
        int freeTrotinetes = 0;
        Map<String,Integer> temp = new HashMap<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                freeTrotinetes = mapa[i][j].getAllTrotinetesLivres().size();
                // TODO: Avisar que o ficheiro com os locais tem 8 locais repetidos!
                temp.put(mapa[i][j].getName(),freeTrotinetes);
            }
        }

        trotLivres = new ArrayList(temp.entrySet());
        Collections.sort(trotLivres, new Comparator<Map.Entry<?, Integer>>(){

            public int compare(Map.Entry<? , Integer> o1, Map.Entry<?, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
    }

    public Local[][] getMapa() {
        Local[][] m = new Local[this.size][this.size];
        try {
            l.lock();
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    m[i][j] = mapa[i][j];
                }
            }
        }
        finally {
            l.unlock();
        }
        return m;
    }

    // TODO: Avisar que tirei o lock neste método porque o nome e a posição dos locais nunca vão mudar ao longo da execução do programa (passei para int porque não há necessidade de usar double)
    public int calculaDistancia(String Origem, String Destino){
        int x1=-1,x2=0,y1=-1,y2=0;

        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (this.mapa[i][j].getName().equals(Origem)) {
                    x1 = this.mapa[i][j].getX();
                    y1 = this.mapa[i][j].getY();
                }
                if (this.mapa[i][j].getName().equals(Destino)) {
                    x2 = this.mapa[i][j].getX();
                    y2 = this.mapa[i][j].getY();
                }
            }
        }
        if(x1==-1 && y1==-1) return -1;
        return (Math.abs(x1-x2)+Math.abs(y1-y2));
    }

    public void geraRecompensas() {
        List<Local> surroundings;
        String origem, destino;
        int valorOrigem, valorDestino, aux = 1, coords, trotinetes = 0, x, y, D = 1;

        for (int i = 0; i < trotLivres.size(); i++) {
            origem = trotLivres.get(i).getKey();
            valorOrigem = trotLivres.get(i).getValue();
            destino = trotLivres.get(trotLivres.size() - aux).getKey();
            valorDestino = trotLivres.get(trotLivres.size() - aux).getValue();

            if (valorOrigem > 1) {
                if (valorDestino == 0) {
                    coords = getCoords(destino);
                    x = coords / 10;
                    y = coords % 10;
                    surroundings = getSurrounding(x, y, D);
                    for (Local l : surroundings) {
                        trotinetes += l.getAllTrotinetesLivres().size();
                    }
                    if (trotinetes == 0)
                        recompensas.put(origem, new Recompensa(origem, destino, calculaDistancia(origem, destino)));
                    else i--;
                    aux++;
                    trotinetes = 0;
                } else break;
            } else break;
        }
    }

    public List<Recompensa> getRecompensaProximidade(String origem,int dist) {
        List<Recompensa> recompensasProx = new ArrayList<>();
        List<Local> proximidade = calculaProximidades(origem, dist);

        for (Local local : proximidade) {
            if (recompensas.containsKey(local.getName()))
                recompensasProx.add(recompensas.get(local.getName()));
        }

        return recompensasProx;
    }

    //lock para garantir que na lista de trotinetes não tenha nenhuma que já mudou para uma posicao que
    public List<Local> calculaProximidades(String origem, int distancia){
        List<Local> locais = new ArrayList<>();

        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if(calculaDistancia(origem,mapa[i][j].getName()) > 0 && calculaDistancia(origem,mapa[i][j].getName()) < distancia)
                    locais.add(mapa[i][j]);
            }
        }
        return locais;
    }

    public List<String> lerFicheiro (String nomeFich) throws FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
        } catch (IOException exc) {
            lines = new ArrayList<>();
        }
        return lines;
    }

    public String showMenu(){
        StringBuilder tabela = new StringBuilder();
        tabela.append("------------------------------------------------------------------------------------------------------------------------------------------");
        tabela.append("|                   LOCAL                            |            COORDENADAS             |             TROTINETES                        |");
        tabela.append("------------------------------------------------------------------------------------------------------------------------------------------");
        int i;
        int j;
        for(i=0;i<20;i++){
            for(j=0;j<20;j++){
                tabela.append("|").append(mapa[i][j].getName()).append("                         | x=").append(mapa[i][j].getX()).append(", y=").append(mapa[i][j].getY()).append("|                          Trotinetes: ").append(mapa[i][j].getAllTrotinetes()).append(" ");
                tabela.append("------------------------------------------------------------------------------------------------------------------------------------------");
            }
        }
        return tabela.toString();
    }

    public String showDisponiveis(List<Local> locais){
        StringBuilder tabela = new StringBuilder();
        int k=0;
        try {
            l.lock();
            for (Local l : locais) {
                if (l.getAllTrotinetesLivres().size() != 0) {
                    k += l.getAllTrotinetesLivres().size();
                    tabela.append("| Local: ").append(l.getName()).append("\n|Coordenadas geográficas: x=").append(l.getX()).append(", y=").append(l.getY()).append("\n|Trotinetes Livres: ").append(l.getAllTrotinetesLivres()).append("\n");
                    tabela.append("------------------------------------------------------------------------------------------------------------------------------------------\n");
                }
            }
            tabela.append("Total de trotinetes disponíveis = ").append(k);
            return tabela.toString();
        }finally {
            l.unlock();
        }
    }
}
