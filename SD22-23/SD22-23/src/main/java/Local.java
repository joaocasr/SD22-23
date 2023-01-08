import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Local {
    private String name;
    private int x;
    private int y;
    private List<Trotinete> allTrotinetes;
    private ReadWriteLock l = new ReentrantReadWriteLock();

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
            l.readLock().lock();
            for(Trotinete t : allTrotinetes){
                if(t.islivre()) trotinetes.add(t);
            }
            return this.allTrotinetes;
        }finally {
            l.readLock().unlock();
        }
    }

    public void removeLivre(Trotinete t){
        try{
            l.writeLock().lock();
            getAllTrotinetesLivres().remove(t);
        }finally {
            l.writeLock().unlock();
        }
    }

    public void addLivre(Trotinete t){
        try{
            l.writeLock().lock();
            getAllTrotinetesLivres().add(t);
        }finally {
            l.writeLock().unlock();
        }
    }

    public List<Trotinete> getAllTrotinetes(){
        try {
            l.readLock().lock();
            return this.allTrotinetes;
        }finally {
            l.readLock().unlock();
        }
    }

    public void adicionaTrotinete(Trotinete t){
        try{
            l.writeLock().lock();
            this.allTrotinetes.add(t);
        }finally {
            l.writeLock().unlock();
        }
    }

    public void removeTrotinete(String codigo){
        try{
            l.writeLock().lock();
            for(Trotinete t : allTrotinetes){
                if(t.getCodigo().equals(codigo)) this.allTrotinetes.remove(t);
            }
        }finally {
            l.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return  "Nome do Local:" + name +"\n"+
                "Coordenadas: x=" + x + ", y=" + y + ",\n"+
                "Trotinetes disponíveis=" + allTrotinetes +
                '\n';
    }
}
