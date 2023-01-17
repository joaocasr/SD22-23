package sd20200129;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class GesFicheiros implements Ficheiros{
    private Map<String,Queue<Ficheiro>> fila = new HashMap<>();
    private Map<String,Ficheiro> ficheiros = new HashMap<>();
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition c = reentrantLock.newCondition();

    private class Ficheiro{
        boolean using;
        boolean modified=false;
        String path;
        Condition condition = reentrantLock.newCondition();

        public Ficheiro(String path) {
            this.path=path;
        }
    }

    public void adicionaQueue(Ficheiro f){
        Queue<Ficheiro> q;
        if(!fila.containsKey(f.path)){
            q = new ArrayDeque<>();
        }else{
            q = fila.get(f.path);
        }
        q.add(f);
        fila.put(f.path,q);
    }

    public void retiraQueue(Ficheiro f){
        fila.get(f.path).poll();
    }

    @Override
    public synchronized void using(String path) {
        try{
            Ficheiro f =ficheiros.get(path);
            adicionaQueue(f);
            if(f.using) {
                while (f.using) {
                    f.condition.wait();
                }
            }
            retiraQueue(f);
            f.using=true;
            ficheiros.put(f.path,f);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public synchronized void notUsing(String path, boolean modified) {
        Ficheiro f = ficheiros.get(path);
        f.modified=modified;
        f.using=false;
        ficheiros.put(f.path,f);
        if(fila.containsKey(path) && fila.get(path).size()!=0){
            fila.get(path).poll().condition.notify();
        }
        c.notify();
    }

    @Override
    public synchronized List<String> startBackup() {
        List<String> copy=new ArrayList<>();
        try{
            while (ficheiros.values().stream().filter(x->!x.using).count()!= ficheiros.size()){
                c.wait();
            }
            copy = ficheiros.values().stream().filter(x->x.modified).map(x->x.path).collect(Collectors.toList());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return copy;
    }

    @Override
    public synchronized void endBackup() {
        for(String f : startBackup()){
            ficheiros.put(f,new Ficheiro(f));
        }
    }
}
