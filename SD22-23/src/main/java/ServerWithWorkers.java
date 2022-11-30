import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerWithWorkers {
    private static final Map<String,TaggedConnection> allUsersConnections=new HashMap<>();
    private UsersFacade users;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        UsersFacade usersFacade = new UsersFacade();
        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            //cada pedido tem uma tag distinta login:0 registo:1
            Runnable worker = () -> {
                try{
                    for (;;) {
                        TaggedConnection.Frame frame = c.receive();
                        int tag = frame.tag;
                        String data = new String(frame.data);
                        if (frame.tag == 0) {//login
                            String[] info = data.split(";");
                            if(!usersFacade.existeUser(info[0])){
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta não existe!".getBytes();
                                c.send(frame.tag, msg);
                            }
                            else if(usersFacade.login(info[0],info[1])){
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "Login efetuado com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            };

            new Thread(worker).start();
        }

    }
}

