import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TextUI {
    private Menu menu = new Menu();
    private static Demultiplexer demultiplexer;
    private static boolean notificacao=false;
    private static boolean spam=false;
    private static int time;
    private static Lock lock = new ReentrantLock();
    private static Condition con = lock.newCondition();

    public TextUI(Demultiplexer demultiplexer){
        this.demultiplexer = demultiplexer;
    }

    public void Menu() throws InterruptedException {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Efetuar Login.\n");
        opcoes.add("Registar Conta.");
        menu.setOptions(opcoes);
        menu.setHandlers(1,this::efetuarLogin);
        menu.setHandlers(2,this::efetuarRegisto);
        menu.run();
    }

    public void MenuSecundario() throws InterruptedException {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Listagem de trotinetes livres.\n");
        opcoes.add("Efetuar reserva da trotinete mais próxima.\n");
        opcoes.add("Estacionamento de trotinete.\n");
        opcoes.add("Ativar Notificacoes.");

        Thread thread = new Thread(()->{
            try {
                trataReceberNotificacoes();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        menu.setOptions(opcoes);
        menu.setHandlers(1,this::listarTrotinetesLivres);
        menu.setHandlers(2,this::trataReservas);
        menu.setHandlers(3,this::estacionamentodeTrotinetes);
        menu.setHandlers(4,this::ativaNotificacoes);
        menu.run();
    }

    public void run() throws IOException, InterruptedException {
        Menu();
        demultiplexer.close();
    }

    public void ativaNotificacoes()throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                if(notificacao) {
                    System.out.println("Pretende desativar as notificações?[S/N]");
                    String r = scanner.nextLine();
                    if(r.equalsIgnoreCase("S")) notificacao=false;
                }else{
                    System.out.println("Pretende ativar as notificações?[S/N]");
                    String r = scanner.nextLine();
                    System.out.println("De quanto em quanto tempo pretende receber notificações? (segundos)");
                    time = scanner.nextInt() * 1000;
                    if(r.equalsIgnoreCase("S")) notificacao=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void trataReceberNotificacoes()throws InterruptedException {
        while (true) {
            //System.out.println("entrou");
            try {
                lock.lock();
                while (!spam || !notificacao) {
                    con.await();
                    //System.out.println("waiting");
                }
                //System.out.println("entrou");
                demultiplexer.send(0, "ola".getBytes());
                byte[] b = demultiplexer.receive(0);
                String notificacao = new String(b);
                System.out.println(notificacao);
                lock.unlock();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread.sleep(time);
        }
    }

    public void efetuarLogin() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam=false;
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();
                String dados = username+";"+password+";";
                demultiplexer.send(1,dados.getBytes());

                byte [] b = demultiplexer.receive(1);
                String response = new String(b);
                System.out.println(response);
                spam=true;
                con.signalAll();
                lock.unlock();
                if(response.contains("sucesso")) MenuSecundario();
        } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void efetuarRegisto() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam=false;
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();
                String dados = username+";"+password+";";
                demultiplexer.send(2,dados.getBytes());

                byte [] b = demultiplexer.receive(2);
                String response = new String(b);
                spam=true;
                con.signalAll();
                lock.unlock();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void listarTrotinetesLivres() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam = false;
                System.out.println("Insira o local: ");
                Scanner scanner = new Scanner(System.in);
                String local = scanner.nextLine();
                System.out.println("Insira o raio de distância (km): ");
                int dist = scanner.nextInt();
                String dados = local+";"+dist+";";
                demultiplexer.send(3,dados.getBytes());

                byte [] b = demultiplexer.receive(3);
                String response = new String(b);
                spam=true;
                con.signalAll();
                lock.unlock();
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void trataReservas() throws InterruptedException{
        Thread t = new Thread(()->{
            try{
                lock.lock();
                spam=false;
                System.out.println("Insira o local:");
                Scanner scanner = new Scanner(System.in);
                String local = scanner.nextLine();
                System.out.println("Insira o raio em km:");
                int distancia = scanner.nextInt();
                String dados = local+";"+distancia+";";
                demultiplexer.send(5,dados.getBytes());

                byte [] b = demultiplexer.receive(5);
                String response = new String(b);
                System.out.println(response);
                spam=true;
                con.signalAll();
                lock.unlock();
            }catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void estacionamentodeTrotinetes() throws InterruptedException{
        Thread t = new Thread(()->{
           try{
               lock.lock();
               spam=false;
               System.out.println("Insira o código de reserva:");
               Scanner scanner = new Scanner(System.in);
               String codigo = scanner.nextLine();
               System.out.println("Insira o local que estacionou:");
               String local = scanner.nextLine();
               String dados = codigo+";"+local+";";
               demultiplexer.send(4,dados.getBytes());

               byte [] b = demultiplexer.receive(4);
               String response = new String(b);
               System.out.println(response);
               spam=true;
               con.signalAll();
               lock.unlock();
           }catch (Exception e) {
               e.printStackTrace();
           }
        });
        t.start();
        t.join();
    }


}