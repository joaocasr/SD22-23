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
    private static boolean notificacao = false;
    private static boolean spam = false;
    private static int time;
    private static Lock lock = new ReentrantLock();
    private static Condition con = lock.newCondition();
    private static String local;
    private static int distancia;
    private static Thread threadNotificacao;
    private static int threadCreated = 0;

    public TextUI(Demultiplexer demultiplexer) {
        this.demultiplexer = demultiplexer;
    }

    public void Menu() throws InterruptedException {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Efetuar Login.\n");
        opcoes.add("Registar Conta.");
        menu.setOptions(opcoes);
        menu.setHandlers(1, this::efetuarLogin);
        menu.setHandlers(2, this::efetuarRegisto);
        menu.run();
    }

    public void MenuSecundario() throws InterruptedException {
        List<String> opcoes = new ArrayList<>();
        opcoes.add("Listagem de trotinetes livres.\n");
        opcoes.add("Efetuar reserva da trotinete mais próxima.\n");
        opcoes.add("Estacionamento de trotinete.\n");
        opcoes.add("Ativar Notificacoes.");

        if (threadCreated == 0) {
            threadNotificacao = new Thread(() -> {
                trataReceberNotificacoes();
            });
            threadCreated++;
        }
        threadNotificacao.start();
        menu.setOptions(opcoes);
        menu.setHandlers(1, this::listarTrotinetesLivres);
        menu.setHandlers(2, this::trataReservas);
        menu.setHandlers(3, this::estacionamentodeTrotinetes);
        menu.setHandlers(4, this::ativaNotificacoes);
        menu.run();
    }

    public void run() throws IOException {
        try {
            Menu();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        demultiplexer.close();
    }

    public void ativaNotificacoes() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Scanner scanner = new Scanner(System.in);
                if (notificacao) {
                    System.out.println("Pretende desativar as notificações?[S/N]");
                    String r = scanner.nextLine();
                    if (r.equalsIgnoreCase("S"))
                        notificacao = false;
                } else {
                    System.out.println("Pretende ativar as notificações?[S/N]");
                    String r = scanner.nextLine();
                    System.out.println("De quanto em quanto tempo pretende receber notificações? (segundos)");
                    time = scanner.nextInt() * 1000;
                    System.out.println("Insira o lugar atual:");
                    scanner.nextLine();
                    local = scanner.nextLine();
                    System.out.println("Insira a distância do local atual para procurar recompensas:");
                    distancia = scanner.nextInt();
                    if (r.equalsIgnoreCase("S"))
                        notificacao = true;
//                    String dados = local + ";" + distancia + ";";
//                    demultiplexer.send(0, dados.getBytes());
//                    byte[] b = demultiplexer.receive(0);
//                    String response = new String(b);
//                    System.out.println(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public static void trataReceberNotificacoes() {
        while (true) {
            //System.out.println("entrou");
            try {
                lock.lock();
                while (!spam || !notificacao) {
                    con.await();
                    //System.out.println("waiting");
                }
                //System.out.println("entrou");
                String dados = local + ";" + distancia + ";";
                System.out.println("TEST" + dados);
                demultiplexer.send(0, dados.getBytes());
                byte[] b = demultiplexer.receive(0);
                String response = new String(b);
                System.out.println(response);
                Thread.sleep(time);
            } catch (InterruptedException e) {
                threadNotificacao.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public void efetuarLogin() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam = false;
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();
                String dados = username + ";" + password + ";";
                demultiplexer.send(1, dados.getBytes());

                byte[] b = demultiplexer.receive(1);
                String response = new String(b);
                System.out.println(response);
                spam = true;
                con.signalAll();
                lock.unlock();
                if (response.contains("sucesso")) MenuSecundario();
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
                spam = false;
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();
                String dados = username + ";" + password + ";";
                demultiplexer.send(2, dados.getBytes());

                byte[] b = demultiplexer.receive(2);
                String response = new String(b);
                spam = true;
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
                String dados = local + ";" + dist + ";";
                demultiplexer.send(3, dados.getBytes());

                byte[] b = demultiplexer.receive(3);
                String response = new String(b);
                spam = true;
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

    public void trataReservas() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam = false;
                System.out.println("Insira o local:");
                Scanner scanner = new Scanner(System.in);
                String local = scanner.nextLine();
                System.out.println("Insira o raio em km:");
                int distancia = scanner.nextInt();
                String dados = local + ";" + distancia + ";";
                demultiplexer.send(5, dados.getBytes());

                byte[] b = demultiplexer.receive(5);
                String response = new String(b);
                System.out.println(response);
                spam = true;
                con.signalAll();
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void estacionamentodeTrotinetes() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                lock.lock();
                spam = false;
                System.out.println("Insira o código de reserva:");
                Scanner scanner = new Scanner(System.in);
                String codigo = scanner.nextLine();
                System.out.println("Insira o local que estacionou:");
                String local = scanner.nextLine();
                String dados = codigo + ";" + local + ";";
                demultiplexer.send(4, dados.getBytes());

                byte[] b = demultiplexer.receive(4);
                String response = new String(b);
                System.out.println(response);
                spam = true;
                con.signalAll();
                lock.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }


}