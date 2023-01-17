package sd20200915;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Atendimento implements SalaDeEspera {
    private Queue<Pessoa> fila;
    private ReentrantLock reentrantLock;

    public Atendimento(){
        this.fila =new ArrayDeque<>();
        this.reentrantLock = new ReentrantLock();
    }

    private class Pessoa{
        String nome;
        boolean desiste;
        Condition condition = reentrantLock.newCondition();
        public Pessoa(String nome){this.nome=nome;}
    }

    @Override
    public boolean espera(String nome) {
        final Pessoa p=new Pessoa(nome);
        p.desiste=false;
        fila.add(p);
        synchronized (p) {
            try {
                System.out.println(p.nome+" está na sala de espera.");
                p.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       return !p.desiste;
    }

    @Override
    public void desiste(String nome) {
        Queue<Pessoa> q = new ArrayDeque<>();
        while(!this.fila.isEmpty()){
            Pessoa p = fila.poll();
            synchronized (p) {
                if (!p.nome.equals(nome)) q.add(p);
                else {
                    p.desiste = true;
                    p.notify();
                }
            }
        }
        this.fila=q;
    }

    @Override
    public String atende() {
        String nome = null;
        if(!this.fila.isEmpty()) {
            Pessoa p = this.fila.poll();
            synchronized (p) {
                p.notify();
            }
            nome=p.nome;
        }
        return nome;
    }



}
