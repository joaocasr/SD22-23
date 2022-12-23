import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;;

public class TaggedConnection {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Lock readLock = new ReentrantLock();
    private Lock writeLock = new ReentrantLock();

    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data) { this.tag = tag; this.data = data; }
    }

    public TaggedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        this.dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.readLock = new ReentrantLock();
        this.writeLock = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        try{
            this.readLock.lock();
            this.dataOutputStream.writeInt(frame.tag);
            this.dataOutputStream.writeInt(frame.data.length);
            this.dataOutputStream.write(frame.data);
            this.dataOutputStream.flush();
        }finally {
            this.readLock.unlock();
        }
    }

    public void send(int tag, byte[] data) throws IOException {
        this.send(new Frame(tag, data));
    }

    public Frame receive() throws IOException {
        try{
            this.writeLock.lock();
            int tag = this.dataInputStream.readInt();
            int N = this.dataInputStream.readInt();
            byte [] dados = new byte[N];
            this.dataInputStream.readFully(dados);
            return new Frame(tag,dados);
        }finally {
            this.writeLock.unlock();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}

