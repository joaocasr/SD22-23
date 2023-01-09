import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mapa {
    private Local [][] mapa;
    private int size;
    private List<String> nomes = new ArrayList<>();
    private List<Trotinete> trotinetes = new ArrayList<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Map<String,String> reservas = new HashMap<>(); // cod.reserva-> cod.trotinete;local
    private Map<String, LocalDateTime> tempoReservas = new HashMap<>();//cod.reserva->tempo
    private Map<String,List<Recompensa>> recompensas = new HashMap<>();
    private ArrayList<Map.Entry<String, Integer>> trotLivres;


    public Mapa() throws FileNotFoundException {
        int N = this.size = 20;
        mapa = new Local[N][N];
        this.nomes = lerFicheiro("/home/alexandre/Documents/SD22-23/SD22-23/src/main/java/locais");
        int k=0;
        int l;
        for(l=0;l<100;l++){
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
        /*for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.println(mapa[i][j]);
            }
        }*/
    }

    /**RESERVAS*/
    public String geracodigoreserva(Trotinete t) {
        return "R"+t.getCodigo().split("C")[1];
    }

    public String tratareserva(String origem, int distancia) {
        Trotinete t = null;
        String localcodigo="";

        List<Local> locais = new ArrayList<>(calculaProximidades(origem, distancia));

        double aux = (double) distancia;
        Local laux = null;
        List<Trotinete> listLivres;
        boolean control = false;

        //try {
        //    readWriteLock.readLock().lock();
            for (Local l : locais) {
                if (calculaDistancia(origem, l.getName()) <= aux) {
                    listLivres = l.getAllTrotinetesLivres();
                    if (listLivres.size() != 0) {
                        t = listLivres.get(0);
                        aux = calculaDistancia(origem, l.getName());
                        laux = l;
                        control = true;
                    }
                }
            }
            if (control) {
                String codigoreserva = geracodigoreserva(t);
                try {
                    readWriteLock.writeLock().lock();
                    reservas.put(codigoreserva, t.getCodigo() + ";" + laux.getName());
                    t.setOcupada();
                    laux.removeLivre(t);
                    tempoReservas.put(codigoreserva, LocalDateTime.now());
                }
                finally {
                    readWriteLock.writeLock().unlock();
                }
                localcodigo = codigoreserva + ";" + laux.getName();
            } else {
                localcodigo = "erro de insucesso -1";
            }
            return localcodigo;
    //    }finally {
    //        readWriteLock.readLock().lock();
    //   }
    }

    public void distribuicaoAleatoria(Trotinete t){
        int x = (int)(Math.random()*(20));
        int y = (int)(Math.random()*(20));
        mapa[x][y].adicionaTrotinete(t);
    }

    public Local getLocal(int x,int y){
        return this.mapa[x][y];
    }

    public Local getLocal(String local){
        int N=20;
        Local l = null;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(mapa[i][j].getName().equals(local)) l= mapa[i][j];
            }
        }
        return l;
    }

    public Local getLocalTrotinete(String codigo){
        int N=20;
        Local l = null;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(mapa[i][j].getAllTrotinetes().stream().anyMatch(x -> x.getCodigo().equals(codigo))) l= mapa[i][j];
            }
        }
        return l;
    }

    public Local[][] getMapa() {
        Local[][] m = new Local[this.size][this.size];
        try {
            readWriteLock.readLock().lock();
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    m[i][j] = mapa[i][j];
                }
            }
        }
        finally {
            readWriteLock.readLock().unlock();
        }
        return m;
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

    public void geraRecompensas() {
        List<Local> surroundings;
        String origem, destino;
        int valorOrigem, valorDestino, aux = 1, coords, trotinetes = 0, x, y, D = 1;
        try {
            readWriteLock.writeLock().lock();
            //recompensas = new HashMap<>();
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
                        if (trotinetes == 0) {
                            if (!recompensas.containsKey(origem)) recompensas.put(origem, new ArrayList<>());
                            boolean notIn = true;
                            for (Recompensa recompensa : recompensas.get(origem)) {
                                if (recompensa.getEnd().equals(destino)) {
                                    notIn = false;
                                    break;
                                }
                            }
                            if (notIn) recompensas.get(origem).add(new Recompensa(origem,destino,calculaDistancia(origem,destino)));
                        }
                        else i--;
                        aux++;
                        trotinetes = 0;
                    } else break;
                } else break;
            }
        }
        finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public String getRecompensaProximidade(String origem,int dist) {
        //List<Recompensa> recompensasProx = new ArrayList<>();
        List<Local> proximidade = calculaProximidades(origem, dist);
        int aux = 0;
        StringBuilder resposta = new StringBuilder();

        try {
            readWriteLock.readLock().lock();
            for (Local local : proximidade) {
                if (recompensas.containsKey(local.getName())) {
                    //recompensasProx.add(recompensas.get(local.getName()));
                    aux++;
                    for (Recompensa recompensa : recompensas.get(local.getName())) {
                        resposta.append(local.getName()).append(" - ").append(recompensa.getEnd()).append(" -> recompensa: ").append(recompensa.getReward()).append('\n');
                    }
                }
            }
            if (aux == 0)
                resposta.append("De momento não existem recompensas num raio de ").append(dist).append(" km de ").append(origem);

            return resposta.toString();
        }
        finally {
            readWriteLock.readLock().unlock();
        }
    }
    /*public int calculaRecompensa(String inicio,String fim){
        //calcular a partir de que raio se encontram trotinetes livres
        System.out.println("CALCULO DE RECOMPENSA");
        Local l1 = getLocal(inicio);
        Local l2 = getLocal(fim);
        System.out.println("inicio:"+inicio);
        System.out.println("fim:"+fim);
        int recompensa=0;
        if(l1.getAllTrotinetesLivres().size()<2){
            System.out.println("FALHOU NA PRIMEIRA CONDICAO");
            System.out.println(l1.getAllTrotinetesLivres().size());
            return recompensa;
        }
        else {
            int i = 1;
            boolean found = false;
            while (!found) {
                List<Local> l = calculaProximidades(l2.getName(),i);
                int numLocais= l.size();
                System.out.println("NUMERO DE LOCAIS PROXIMOS:"+numLocais);
                if(l.stream().filter(x->x.getAllTrotinetesLivres().size()==0).count()==numLocais){
                    l.forEach(System.out::println);
                    recompensa++;
                    System.out.println("+1");
                }else{
                    found=true;
                    System.out.println("ACABOU");
                }
                i++;
            }
        }
        recompensas.put(l1.getName()+"-"+l2.getName(),recompensa);
        System.out.println(recompensa);
        return recompensa;
    }*/

    public double calculaDistancia(String Origem, String Destino){
        int x1=0,x2=0,y1=0,y2=0;

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
        return (Math.abs(x1-x2)+Math.abs(y1-y2));
    }

    public List<Local> calculaProximidades(String origem, int distancia){
        List<Local> locais = new ArrayList<>();
        try {
            readWriteLock.readLock().lock();
            if (getLocal(origem) != null) {
                for (int i = 0; i < this.size; i++) {
                    for (int j = 0; j < this.size; j++) {
                        if (calculaDistancia(origem, mapa[i][j].getName()) >= 0 && calculaDistancia(origem, mapa[i][j].getName()) <= distancia)
                            locais.add(mapa[i][j]);
                    }
                }
            }
            if (locais.size() == 0) System.out.println("erro");
            return locais;
        }
        finally {
            readWriteLock.readLock().unlock();
        }
    }

    public double calculaCusto(String codReserva,String local) throws myExceptions.IncorrectDestinationName, myExceptions.IncorrectReservationCode {
        System.out.println("TRATA ESTACIONAMENTO");
        Local chegada = getLocal(local);
        try {
            readWriteLock.readLock().lock();
            if (chegada != null && reservas.containsKey(codReserva)) {
                String codigo = reservas.get(codReserva).split(";")[0];//codigo trotinete
                System.out.println(codigo);
                Local partida = getLocal(reservas.get(codReserva).split(";")[1]);
                System.out.println(partida);
                LocalDateTime beginTime = tempoReservas.get(codReserva);
                System.out.println(beginTime);
                long time = ChronoUnit.SECONDS.between(beginTime, LocalDateTime.now());
                System.out.println("partida:" + partida.getName() + " chegada" + chegada.getName());
                double distancia = calculaDistancia(partida.getName(), chegada.getName());
                System.out.println((time * 0.15) / distancia);
                List<Trotinete> list = chegada.getAllTrotinetes();
                for (Trotinete t : list) {
                    if (codigo.equals(t.getCodigo())) {
                        t.setlivre();
                    }
                }
                return ((time * 0.15) + distancia) + 0.5;
            } else if (chegada == null) {
                throw new myExceptions.IncorrectDestinationName("Local de estacionamento indicado é inválido!");
            } else {
                throw new myExceptions.IncorrectReservationCode("Código de reserva inválido!");
            }
        }
        finally {
            readWriteLock.readLock().unlock();
        }
    }

    /*public int devolveRecompensa(String codreserva,String local){
        Local chegada = getLocal(local);
        String codigo = reservas.get(codreserva).split(";")[0];//codigo trotinete
        System.out.println("CODIGO:"+codigo);
        System.out.println("chegada: "+chegada.getName());
        Local partida = getLocal(reservas.get(codreserva).split(";")[1]);
        System.out.println("partida: "+partida.getName());
        int r = calculaRecompensa(partida.getName(),chegada.getName());
        System.out.println(chegada.getAllTrotinetesLivres().size());
        Trotinete t = trotinetes.stream().filter(x->x.getCodigo().equals(codigo)).findFirst().get();
        t.setlivre();
        chegada.addLivre(t);
        System.out.println(chegada.getAllTrotinetesLivres().size());
        return r;
    }*/

    public List<String> lerFicheiro (String nomeFich) throws FileNotFoundException {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(nomeFich), StandardCharsets.UTF_8);
        } catch (IOException exc) {
            lines = new ArrayList<>();
        }
        return lines;
    }

    public double findReward(String codReserva, String destino) {
        Local chegada = getLocal(destino);
        String codigo = reservas.get(codReserva).split(";")[0];//codigo trotinete
        System.out.println(codigo);
        Local partida = getLocal(reservas.get(codReserva).split(";")[1]);
        System.out.println(partida);
        double reward = 0;

        try {
            readWriteLock.writeLock().lock();
            reservas.remove(codReserva);
            if (recompensas.containsKey(partida.getName())) {
                for (Recompensa recompensa : recompensas.get(partida.getName())) {
                    if (recompensa.getEnd().equals(chegada.getName())) {
                        reward = recompensa.getReward();
                        recompensas.get(partida.getName()).remove(recompensa);
                        break;
                    }
                }
            }
            return reward;
        }
        finally {
            readWriteLock.writeLock().unlock();
        }
    }


    /*
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
    }*/

    public String showDisponiveis(List<Local> locais){
        StringBuilder tabela = new StringBuilder();
        int k=0;
        try {
            readWriteLock.writeLock().lock();
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
            readWriteLock.writeLock().unlock();
        }
    }
}
