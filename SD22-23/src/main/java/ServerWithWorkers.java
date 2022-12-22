import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerWithWorkers {
    private static final Map<String,TaggedConnection> allUsersConnections=new HashMap<>();
    private static UsersFacade users;
    private static Mapa mapa;

    public static void main(String[] args) throws Exception {
        System.out.println("Executing...");
        ServerSocket ss = new ServerSocket(12345);
        users = new UsersFacade();
        mapa = new Mapa();

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
                        if(frame.tag==0){}
                        else if (frame.tag == 1) {      //LOGIN

                            String[] info = data.split(";");
                            System.out.println(info[0]+","+info[1]);
                            if(!users.existeUser(info[0])){
                                System.out.println(users.existeUser(info[0]));
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
                        else if(frame.tag == 4){
                            String[] info = data.split(";");
                            System.out.println(info[0] + "," + info[1]);
                            double custo = mapa.trataEstacionamento(info[0],info[1]);
                            int recompensa = mapa.devolveRecompensa(info[0],info[1]);
                            String response = "Estacionamento efetuado com sucesso!\n O custo da viagem é de"+custo+".\nRecompensa:"+recompensa;
                            c.send(frame.tag,response.getBytes());

                        }else if(frame.tag == 5){
                            String[] info = data.split(";");
                            System.out.println(info[0] + "," + info[1]);
                            String localcodigo = mapa.tratareserva(info[0], Integer.parseInt(info[1]));
                            System.out.println(localcodigo);
                            String local = localcodigo.split(";")[1];
                            String codigo = localcodigo.split(";")[0];
                            String response= "Local: "+local+" Código de Reserva:"+codigo;
                            c.send(frame.tag,response.getBytes());
                        }
                    }
                } catch (Exception e) {

                }
            };

            new Thread(worker).start();
        }

    }
}

