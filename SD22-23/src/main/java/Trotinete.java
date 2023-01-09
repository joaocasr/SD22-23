import java.util.concurrent.locks.ReentrantLock;

public class Trotinete {
    private boolean livre;
    private String codigo;

    public Trotinete(String codigo,boolean livre){
        this.livre = livre;
        this.codigo = codigo;
    }

    public boolean islivre() {
        return this.livre;
    }

    public void setlivre(){
        this.livre=true;
    }

    public void setOcupada(){
        this.livre=false;
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
