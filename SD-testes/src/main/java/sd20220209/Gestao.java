package sd20220209;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Gestao implements Reuniao{
    private Sala sala;
    private ReentrantLock reentrantLock;
    private Condition condition;
    private ReadWriteLock readWriteLock;

    public Gestao(){
        this.sala= new Sala();
        this.reentrantLock = new ReentrantLock();
        this.condition = reentrantLock.newCondition();
        this.readWriteLock = new ReentrantReadWriteLock();
    }

    @Override
    public void participa(int lista) {
        if(sala.getLista() == null) {
            try {
                reentrantLock.lock();//garantir que duas listas não facam reserva da sala ao mesmo tempo
                sala.setLista(new Lista(lista));
                sala.adicionaMembro();
            } finally {
                reentrantLock.unlock();
            }
        } else{
            try {
                reentrantLock.lock();
                sala.adicionaEspera();
                while (sala.getLista().getDesignacao() != lista) {
                    condition.await();
                }
                sala.setLista(new Lista(lista));
                sala.adicionaMembro();
            }catch (InterruptedException i){
                System.out.println(i.getMessage());
            }finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override
    public void abandona(int lista) {
        Thread thread = new Thread(()->{
            if(sala.getLista().getDesignacao()==lista){
                try{
                    reentrantLock.lock();
                    if(sala.getPresentes()==1){
                        sala.setLista(null);
                    }
                    sala.removeMembro();
                    condition.signal();
                }finally {
                    reentrantLock.unlock();
                }
            }
        });
        thread.start();
    }

    @Override
    public int naSala() {
        try{
            readWriteLock.writeLock().lock();
            return sala.getPresentes();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public int aEspera() {
        try{
            readWriteLock.writeLock().lock();
            return sala.getAespera();
        }finally {
            readWriteLock.writeLock().unlock();
        }
    }
}
