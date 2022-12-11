import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerWithWorkers {
    private static final Map<String,TaggedConnection> allUsersConnections=new HashMap<>();
    private static UsersFacade users;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        users = new UsersFacade();
        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            //cada pedido tem uma tag distinta login:1 registo:2
            Runnable worker = () -> {
                try{
                    for (;;) {
                        TaggedConnection.Frame frame = c.receive();
                        int tag = frame.tag;
                        String data = new String(frame.data);
                        if(frame.tag==0){}
                        else if (frame.tag == 1) {      //LOGIN

                            String[] info = data.split(";");
                            if(!users.existeUser(info[0])){
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta não existe!".getBytes();
                                c.send(frame.tag, msg);
                            }
                            else if(users.login(info[0],info[1])){
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "Login efetuado com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }else {
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A password ou o username estão incorretos!".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }else if(frame.tag == 2){       // REGISTO

                            String[] info = data.split(";");
                            if(users.registarUser(info[0],info[1])){
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta foi registada com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }else{
                                allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "Já existe uma conta com o mesmo username.".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            };

            new Thread(worker).start();
        }

    }
}

