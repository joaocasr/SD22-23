import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Local {
    private String name;
    private int x;
    private int y;
    private List<Trotinete> allTrotinetes;
    private ReentrantLock l = new ReentrantLock();

    public Local(String nome,int x, int y) {
        this.name = nome;
        this.x = x;
        this.y = y;
        allTrotinetes = new ArrayList<>();
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Trotinete> getAllTrotinetesLivres(){
        List<Trotinete> trotinetes=new ArrayList<>();
        try {
            l.lock();
            for(Trotinete t : allTrotinetes){
                if(t.islivre()) trotinetes.add(t);
            }
            return this.allTrotinetes;
        }finally {
            l.unlock();
        }
    }

    public void removeLivre(Trotinete t){
        try{
            l.lock();
            getAllTrotinetesLivres().remove(t);
        }finally {
            l.unlock();
        }
    }

    public void addLivre(Trotinete t){
        try{
            l.lock();
            getAllTrotinetesLivres().add(t);
        }finally {
            l.unlock();
        }
    }

    public List<Trotinete> getAllTrotinetes(){
        return this.allTrotinetes;
    }

    public void adicionaTrotinete(Trotinete t){
         this.allTrotinetes.add(t);
    }

    @Override
    public String toString() {
        return  "Nome do Local:" + name +"\n"+
                "Coordenadas: x=" + x + ", y=" + y + ",\n"+
                "Trotinetes disponíveis=" + allTrotinetes +
                '\n';
    }
}
