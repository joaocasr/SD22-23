import java.net.Socket;

public class Client {
    public static void main(String [] args) throws Exception{
        Socket socket = new Socket("localhost", 12345);
        Demultiplexer demultiplexer = new Demultiplexer(new TaggedConnection(socket));
        demultiplexer.start();
        new TextUI(demultiplexer).run();
    }
}
