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
        try{
            l.lock();
            return this.name;
        }finally {
            l.unlock();
        }
    }

    public void setName(String name){
        try{
            l.lock();
            this.name=name;
        }finally {
            l.unlock();
        }
    }

    public int getX() {
        try{
            l.lock();
            return x;
        }finally {
            l.unlock();
        }
    }

    public void setX(int x) {
        try {
            l.lock();
            this.x = x;
        }finally {
            l.unlock();
        }
    }

    public int getY() {
        try {
            l.lock();
            return y;
        }finally {
            l.unlock();
        }
    }

    public void setY(int y) {
        try {
            l.lock();
            this.y = y;
        }finally {
            l.unlock();
        }
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
        try {
            l.lock();
            return this.allTrotinetes;
        }finally {
            l.unlock();
        }
    }

    public void adicionaTrotinete(Trotinete t){
        try{
            l.lock();
            this.allTrotinetes.add(t);
        }finally {
            l.unlock();
        }
    }

    public void removeTrotinete(String codigo){
        try{
            l.lock();
            for(Trotinete t : allTrotinetes){
                if(t.getCodigo().equals(codigo)) this.allTrotinetes.remove(t);
            }
        }finally {
            l.unlock();
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
