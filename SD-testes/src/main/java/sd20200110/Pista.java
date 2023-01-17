package sd20200110;

public class Pista {
    private int num;
    private boolean livre;

    public Pista(int n){
        this.num=n;
        this.livre = true;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isLivre() {
        return livre;
    }

    public void setLivre(boolean livre) {
        this.livre = livre;
    }
}
