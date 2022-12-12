import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Mapa {
    private Local [][] mapa;
    private int size;
    private List<String> nomes = new ArrayList<>();
    private List<Trotinete> trotinetes = new ArrayList<>();
    private ReentrantLock l = new ReentrantLock();

    public Mapa() throws FileNotFoundException {
        int N = this.size = 20;
        mapa = new Local[N][N];
        this.nomes = lerFicheiro("/home/joao/SD22-23/SD22-23/src/main/java/locais");
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


    public double calculaDistancia(String Origem, String Destino){
        int x1=-1,x2=0,y1=-1,y2=0;
        try {
            l.lock();
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
        }finally {
            l.unlock();
        }
    }

    //lock para garantir que na lista de trotinetes não tenha nenhuma que já mudou para uma posicao que
    public List<Local> calculaProximidades(String origem, int distancia){
        List<Local> locais = new ArrayList<>();
        try {
            l.lock();
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    if(calculaDistancia(origem,mapa[i][j].getName())>0 && calculaDistancia(origem,mapa[i][j].getName())<distancia) locais.add(mapa[i][j]);
                }
            }
        }finally {
            l.unlock();
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
