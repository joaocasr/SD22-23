package sd20190726;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class Reune implements Museu{
    private Queue<Guia> guias = new ArrayDeque<>();
    private Queue<Visitante> visitantesPT = new ArrayDeque<>();
    private Queue<Visitante> visitantesEN = new ArrayDeque<>();
    private Queue<Visitante> visitantesPL = new ArrayDeque<>();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private int i=0;
    private int j=0;
    private final Condition conditionvisitantes = reentrantLock.newCondition();

    private class Visitante{}

    private class Guia{
        Condition conditionguias = reentrantLock.newCondition();
    }

    public Guia getGuia(){
        Guia g = null;
        if(this.guias.size()!=0) {
            g = this.guias.element();
        }
        return g;
    }

    @Override
    public boolean enterPT() {
            try{
                reentrantLock.lock();
                Guia g;
                this.visitantesPT.add(new Visitante());
                while ((this.visitantesPT.size()<=1 || this.visitantesPT.size()>3)  || (g=getGuia())==null){
                    conditionvisitantes.await();
                }
                g.conditionguias.signal();
                reentrantLock.lock();
                i++;
                if(i==visitantesPT.size()){
                    guias.remove(g);
                    this.visitantesPT = new ArrayDeque<>();
                }
                reentrantLock.unlock();
                i=0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        return true;
    }

    @Override
    public boolean enterEN() {
        try{
            reentrantLock.lock();
            Guia g;
            this.visitantesEN.add(new Visitante());
            while ((this.visitantesEN.size()<=1 || this.visitantesEN.size()>3)  || (g=getGuia())==null){
                conditionvisitantes.await();
            }
            g.conditionguias.signal();
            reentrantLock.lock();
            j++;
            if(j==visitantesEN.size()){
                guias.remove(g);
                this.visitantesEN = new ArrayDeque<>();
            }
            reentrantLock.unlock();
            j=0;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        return true;
    }
  /*
    @Override
    public void enterPoly() {
        try{
            reentrantLock.lock();
            numVisitantesEN++;
            while (numVisitantesEN<NUM){
                condition.await();
            }
            System.out.println("HELLO! LET'S START THE VISIT");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
    }
*/

    @Override
    public void enterGuide() {
        try {
            reentrantLock.lock();
            Guia g = new Guia();
            this.guias.add(g);
            while ((this.visitantesPT.size()<=1 || this.visitantesPT.size()>3)){
                g.conditionguias.await();
            }
            conditionvisitantes.signalAll();
            System.out.println("Acordeii guia");
            System.out.println("estou a ser requisitado.bye");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }

    }
}
