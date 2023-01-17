package sd20210721;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Gestao implements ControloVacinas{
    private final int NUM = 5;
    private int ready; //23
    private int frascos; //2
    private int lote;
    private Utente [] utentes = new Utente [100];
    private ReentrantLock reentrantLock = new ReentrantLock();

    private class Utente{
        private Condition condition = reentrantLock.newCondition();
    }

    public Gestao(){
        int i;
        for(i=0;i<100;i++){
            utentes[i]=null;
        }
    }

    @Override
    public void pedirParaVacinar() {
        Thread thread = new Thread(()->{
            try {
                reentrantLock.lock();
                Utente u = new Utente();
                utentes[ready++] = u;
                while (ready % NUM != 0 && ready / NUM != frascos) {
                    u.condition.await();
                }
                utentes[ready]=null;
                ready--;
                lote++;
                if(lote==NUM){
                    frascos--;
                    lote=0;
                }
            }catch (InterruptedException i){
                System.out.println(i.getMessage());
            }finally {
                reentrantLock.unlock();
            }
        });
        thread.start();
    }

    @Override
    public void fornecerFrascos(int frascos) {
        try{
            reentrantLock.lock();
            this.frascos += frascos;
            int i;
            int N= ready/NUM;
            while(N>0) {
                for (i = 0; i < 100; i++) {
                    if (utentes[i] != null) break;
                }
                utentes[i].condition.signal();
                N= ready/NUM;
            }
        }finally {
            reentrantLock.unlock();
        }
    }
}
