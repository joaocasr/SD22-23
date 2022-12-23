import java.util.concurrent.locks.ReentrantLock;

public class Trotinete {
    private boolean livre;
    private String codigo;
    private ReentrantLock reentrantLock = new ReentrantLock();

    public Trotinete(String codigo,boolean livre){
        this.livre = livre;
        this.codigo = codigo;
    }

    public boolean islivre() {
        try{
            reentrantLock.lock();
            return this.livre;
        }finally {
            reentrantLock.unlock();
        }
    }

    public void setlivre(){
        try{
            reentrantLock.lock();
            this.livre=true;
        }finally {
            reentrantLock.unlock();
        }
    }

    public void setOcupada(){
        try{
            reentrantLock.lock();
            this.livre=false;
        }finally {
            reentrantLock.unlock();
        }
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return "Código=" + codigo  +
                "-> LIVRE=" + livre;
    }
}
