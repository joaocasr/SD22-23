import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerWithWorkers {
    private static UsersFacade users;
    private static Mapa mapa;
    private static Thread threadnot=null;
    private static ReentrantLock l = new ReentrantLock();
    private static Condition reward = l.newCondition();
    private static int wait;
    private static boolean estacionamento = false;

    public static void main(String[] args) throws Exception {
        System.out.println("Executing...");
        ServerSocket ss = new ServerSocket(12345);
        users = new UsersFacade();
        mapa = new Mapa();

        new Thread(() -> {
            while (true) {
                mapa.initTrotLivres();
                mapa.geraRecompensas(estacionamento);
                wait = 1;
                while (wait == 1) {
                    try {
                        l.lock();
                        reward.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finally {
                        l.unlock();
                    }
                }
            }
        }).start();

        while(true) {
            System.out.println("Running...");
            Socket s = ss.accept();
            TaggedConnection c = new TaggedConnection(s);

            //cada pedido tem uma tag distinta login:1 registo:2 listagem:3
            Runnable worker = () -> {
                try{
                    for (;;) {
                        TaggedConnection.Frame frame = c.receive();
                        int tag = frame.tag;
                        String data = new String(frame.data);
                        if(frame.tag==0){
                            Thread thread = new Thread(()->{
                                String[] info = data.split(";");
                                System.out.println("data "+info[0]+" >< "+info[1]);
                                String response = "\n------NOTIFICAÇÃO------\n" + mapa.getRecompensaProximidade(info[0], Integer.parseInt(info[1]));
                                try {
                                    c.send(0, response.getBytes());
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            });
                            if(threadnot!=null) threadnot.interrupt();
                            threadnot=thread;
                            thread.start();

                        }
                        else if (frame.tag == 1) {      //LOGIN
                            String[] info = data.split(";");
                            System.out.println(info[0]+","+info[1]);
                            if(!users.existeUser(info[0])){
                                System.out.println(users.existeUser(info[0]));
                                //allUsersConnections.put(info[0],c);
                                byte[] msg = new byte[50];
                                msg = "A conta não existe!".getBytes();
                                c.send(frame.tag, msg);
                            }
                            else if(users.login(info[0],info[1])){
                                byte[] msg = new byte[50];
                                msg = "Login efetuado com sucesso!".getBytes();
                                System.out.println("A enviar confirmacao");
                                c.send(frame.tag, msg);
                            }else {
                                byte[] msg = new byte[50];
                                msg = "A password ou o username estão incorretos!".getBytes();
                                c.send(frame.tag, msg);
                            }
                        }else if(frame.tag == 2){       // REGISTO

                            String[] info = data.split(";");
                            if(users.registarUser(info[0],info[1])){
                                byte[] msg = new byte[50];
                                msg = "A conta foi registada com sucesso!".getBytes();
                                c.send(frame.tag, msg);
                            }else{
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
                        else if(frame.tag == 4){    //estacionar
                            String[] info = data.split(";");
                            System.out.println("tag4"+info[0] + "," + info[1]);
                            try {
                                double custo = mapa.calculaCusto(info[0], info[1]); //reserva local
                                double r = mapa.findReward(info[0],info[1]);
                                //int recompensa = mapa.devolveRecompensa(info[0],info[1]);
                                //System.out.println(recompensa);
                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                //System.out.println(custo+"eur");
                                String response = "Estacionamento efetuado com sucesso!\nCusto da viagem:" + decimalFormat.format(custo) + "€.\nRecompensa:" + decimalFormat.format(r) + "€";
                                try {
                                    l.lock();
                                    wait = 0;
                                    estacionamento = true;
                                    reward.signalAll();
                                }
                                finally {
                                    l.unlock();
                                }

                                c.send(frame.tag, response.getBytes());
                            }
                            catch (myExceptions.IncorrectDestinationName | myExceptions.IncorrectReservationCode ex) {
                                c.send(frame.tag, ex.getMessage().getBytes());
                            }
                        }else if(frame.tag == 5){
                            String[] info = data.split(";");
                            System.out.println(info[0] + "," + info[1]);
                            String localcodigo = mapa.tratareserva(info[0], Integer.parseInt(info[1]));
                            System.out.println(localcodigo);
                            String response="";
                            if(!localcodigo.equals("erro de insucesso -1")) {
                                String local = localcodigo.split(";")[1];
                                String codigo = localcodigo.split(";")[0];
                                response = "Local: " + local + " \nCódigo de Reserva:" + codigo;
                            }else response = localcodigo;
                            try {
                                l.lock();
                                wait = 0;
                                estacionamento = false;
                                reward.signalAll();
                            }
                            finally {
                                l.unlock();
                            }
                            c.send(frame.tag,response.getBytes());
                        }else if(frame.tag == 6) {
                            String[] info = data.split(";");
                            System.out.println(info[0] + "," + info[1]);
                            String response = "\n------RECOMPENSAS------\n" + mapa.getRecompensaProximidade(info[0], Integer.parseInt(info[1]));
                            c.send(6,response.getBytes());
                        }
                    }
                } catch (Exception e) {

                }
            };
            new Thread(worker).start();
        }

    }
}

