import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {
    private TaggedConnection taggedConnection;
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Map<Integer,Entrada> map = new HashMap<>();
    private IOException exception = null;

    public class Entrada{
        int waiters=0;
        Deque<byte[]> queue = new ArrayDeque<>();
        Condition condition = reentrantLock.newCondition();
    }

    public Demultiplexer(TaggedConnection conn) {
        this.taggedConnection = conn;
    }

    public void start() {
        new Thread(()->{
            try {
                while (true) {
                    TaggedConnection.Frame frame = this.taggedConnection.receive();
                    reentrantLock.lock();
                    try {
                        Entrada entrada = map.get(frame.tag);
                        if (entrada == null) {
                            entrada = new Entrada();
                            map.put(frame.tag, new Entrada());
                        }
                        entrada.queue.add(frame.data);
                        entrada.condition.signal();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }catch (IOException e) {
                reentrantLock.lock();
                try {
                    exception=e;
                    map.forEach((key,value)->value.condition.signalAll());
                }finally {
                    reentrantLock.unlock();
                }
            }
        }).start();
    }

    public void send(TaggedConnection.Frame frame) throws IOException {
        this.taggedConnection.send(frame);
    }
    public void send(int tag, byte[] data) throws IOException {
        this.taggedConnection.send(tag,data);
    }
    public byte[] receive(int tag) throws IOException, InterruptedException {
        reentrantLock.lock();
        try {
            Entrada m = map.get(tag);
            if(m==null){
                m=new Entrada();
                map.put(tag,m);
            }
            m.waiters++;
            while (true) {
                if (!m.queue.isEmpty()) {
                    byte[] data = m.queue.poll();
                    m.waiters--;
                    if (m.queue.isEmpty() && m.waiters == 0) map.remove(tag);
                    return data;
                }
                if (exception != null) throw exception;
                m.condition.await();
            }
        }finally {
            reentrantLock.unlock();
        }
    }

    public void close() throws IOException {
        this.taggedConnection.close();
    }
}