import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerWithWorkers {
    private static final Map<String,TaggedConnection> ALL_USER_CONNECTIONS = new HashMap<>();
    private static UsersFacade users;
    private static Mapa mapa;
    private static ReadWriteLock readWriteLockUsers = new ReentrantReadWriteLock();
    private static ReadWriteLock readWriteLockTrotinetes = new ReentrantReadWriteLock();
    private static Condition reward = readWriteLockTrotinetes.writeLock().newCondition();
    private static int wait;

    // TODO: Vamos ter de muita coisa com os locks! Eu depois falo melhor com vocês no Discord

    public static void main(String[] args) throws Exception {
        System.out.println("Executing...");
        ServerSocket ss = new ServerSocket(12345);
        users = new UsersFacade();
        mapa = new Mapa();

        new Thread(() -> {
            while (true) {
                try {
                    readWriteLockTrotinetes.writeLock().lock();
                    mapa.initTrotLivres();
                    mapa.geraRecompensas();
                    wait = 1;
                }
                finally {
                    readWriteLockTrotinetes.writeLock().unlock();
                }

                while (wait == 1) {
                    try {
                        reward.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        while(true) {
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            //cada pedido tem uma tag distinta login:1 registo:2 listagem:3
            Runnable worker = () -> {
                try{
                    for (;;) {
                        TaggedConnection.Frame frame = c.receive();
                        int tag = frame.tag;
                        String data = new String(frame.data);
                        if(frame.tag==0){}
                        else if (frame.tag == 1) {      //LOGIN

                            String[] info = data.split(";");
                            System.out.println(info[0]+","+info[1]);
                            if(!users.existeUser(info[0])){
                                System.out.println(users.existeUser(info[0]));
                                ALL_USER_CONNECTIONS.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta não existe!".getBytes();
                                c.send(frame.tag, msg);
                            }
                            else if(users.login(info[0],info[1])){
                                ALL_USER_CONNECTIONS.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "Login efetuado com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }else {
                                ALL_USER_CONNECTIONS.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A password ou o username estão incorretos!".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }else if(frame.tag == 2){       // REGISTO

                            String[] info = data.split(";");
                            if(users.registarUser(info[0],info[1])){
                                ALL_USER_CONNECTIONS.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta foi registada com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }else{
                                ALL_USER_CONNECTIONS.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "Já existe uma conta com o mesmo username.".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }else if(frame.tag == 3) {       // LISTAR TROTINETES LIVRES NUM RAIO DE x KM

                            String[] info = data.split(";");
                            System.out.println(info[0] + "," + info[1]);
                            List<Local> l = mapa.calculaProximidades(info[0], Integer.parseInt(info[1]));
                            if (l.size() == 0) {
                                c.send(frame.tag, ("O local que digitou não se encontra no sistema (verifique a sintaxe).").getBytes());
                            }else {
                                String tabela = mapa.showDisponiveis(l);
                                c.send(frame.tag, tabela.getBytes());
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

