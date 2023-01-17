package sd20220119;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Eleicoes implements Votacao {
    private List<Cabine> cabines;
    private Map<Integer,Boolean> verificados = new HashMap<>();
    private List<Integer> verificadosaux = new ArrayList<>();
    private ReentrantLock reentrantLock = new ReentrantLock();
    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    private Queue<Votante> filaespera = new ArrayDeque<>();
    private Map<Integer,Integer> partidos = new HashMap<>();

    private class Votante{
        Condition c  = reentrantLock.newCondition();
        int identidade;
        public Votante(){}
    }

    public Cabine getCabine(){
        Cabine cabine = null;
        for(Cabine c: cabines){
            if(!c.getOcupada()) cabine=c;
        }
        return cabine;
    }

    @Override
    public boolean verifica(int identidade) {
        try{
            l.writeLock().lock();
            if (verificados.get(identidade)) return false;
            else{
                verificados.put(identidade,true);
                verificadosaux.add(identidade);
            }
        }finally {
            l.writeLock().unlock();
        }
        return true;
    }

    @Override
    public int esperaPorCabine() {
        Cabine cabine=null;
        int id=-1;
        try {
            this.reentrantLock.lock();
            Votante v = new Votante();
            this.filaespera.add(v);
            while ((cabine=getCabine())==null){
                v.c.await();
            }
            id = cabine.getNumero();
            cabine.setOcupada(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.reentrantLock.unlock();
        }
        return id;
    }

    @Override
    public void vota(int escolha) {
        try{
            reentrantLock.lock();
            int n = partidos.get(escolha);
            n=n+1;
            partidos.put(escolha,n);
        }finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public void desocupaCabine(int i) {
        try{
            reentrantLock.lock();
            for (Cabine cabine: cabines){
                if(cabine.getNumero()==i) cabine.setOcupada(false);
            }
            this.filaespera.poll().c.signal();
        }finally {
            reentrantLock.unlock();
        }
    }

    @Override
    public int vencedor() {
        int v=-1;
        try {
            l.writeLock().lock();
            v = partidos.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        }finally {
            l.writeLock().unlock();
        }
        return v;
    }
}
