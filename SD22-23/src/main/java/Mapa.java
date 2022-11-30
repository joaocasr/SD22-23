import java.util.concurrent.locks.ReentrantLock;

public class Mapa {
    private Local [][] mapa;
    private int size;
    private ReentrantLock l = new ReentrantLock();;

    public Mapa(){
        int N = this.size = 20;
        mapa = new Local[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                mapa[i][j] = new Local(i,j);
            }
        }
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


    public double calculaDistancia(String OrigDest){
        int x1=0,x2=0,y1=0,y2=0;
        String [] local = OrigDest.split("-");
        try {
            l.lock();
            for (int i = 0; i < this.size; i++) {
                for (int j = 0; j < this.size; j++) {
                    if (this.mapa[i][j].getName().equals(local[0])) {
                        x1 = this.mapa[i][j].getX();
                        y1 = this.mapa[i][j].getY();
                    }
                    if (this.mapa[i][j].getName().equals(local[1])) {
                        x2 = this.mapa[i][j].getX();
                        y2 = this.mapa[i][j].getY();
                    }
                }
            }
            return (Math.abs(x1-x2)+Math.abs(y1-y2));
        }finally {
            l.unlock();
        }
    }
}
