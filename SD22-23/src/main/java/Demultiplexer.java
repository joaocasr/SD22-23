import java.io.IOException;

public class Demultiplexer implements AutoCloseable{
    private TaggedConnection taggedConnection;
    public Demultiplexer(TaggedConnection conn) {
        this.taggedConnection=conn;
    }
    public void start() {  }
    public void send(TaggedConnection.Frame frame) throws IOException {  }
    public void send(int tag, byte[] data) throws IOException {  }
    //public byte[] receive(int tag) throws IOException, InterruptedException {  }
    @Override
    public void close() throws Exception {

    }
}
