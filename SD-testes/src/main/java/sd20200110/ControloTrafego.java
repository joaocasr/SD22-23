package sd20200110;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ControloTrafego implements ControloTrafegoAereo{
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Map<Integer,Pista> pistas = new HashMap<>();

    private class Aviao{
        int identificador;
        Condition c = reentrantLock.newCondition();
    }

    private Pista getPista(){
        Pista pista = null;
        for(Pista p: pistas.values()){
            if(p.isLivre()){
                pista=p;
                break;
            }
        }
        return pista;
    }

    @Override
    public int pedirParaDescolar() {
        int pista=-1;
        try{
            reentrantLock.lock();
            Aviao a = new Aviao();
            while (getPista()==null){
                a.c.wait();
            }
            Pista p = getPista();
            pista = p.getNum();
            p.setLivre(false);
            pistas.put(pista,p);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        return pista;
    }

    @Override
    public int pedirParaAterrar() {
        int pista=-1;
        try{
            reentrantLock.lock();
            Aviao a = new Aviao();
            while (getPista()==null){
                a.c.wait();
            }
            Pista p = getPista();
            pista = p.getNum();
            p.setLivre(false);
            pistas.put(pista,p);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        return pista;
    }

    @Override
    public void descolou(int pista) {
        Pista p =pistas.get(pista);
        p.setLivre(true);
        pistas.put(pista,p);
        notifyAll();
    }

    @Override
    public void aterrou(int pista) {
        Pista p =pistas.get(pista);
        p.setLivre(false);
        pistas.put(pista,p);
        notifyAll();
    }
}
