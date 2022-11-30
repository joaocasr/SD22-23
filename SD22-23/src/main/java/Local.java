import java.util.concurrent.locks.ReentrantLock;

public class Local {
    private String name;
    private int x;
    private int y;
    private boolean trotinete;
    private ReentrantLock l = new ReentrantLock();

    public Local(int x, int y) {
        this.x = x;
        this.y = y;
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

    public boolean isTrotinete() {
        try {
            l.lock();
            return trotinete;
        }finally {
            l.unlock();
        }
    }

    public void setTrotinete(boolean trotinete) {
        try {
            l.lock();
            this.trotinete = trotinete;
        }finally {
            l.unlock();
        }
    }
}
