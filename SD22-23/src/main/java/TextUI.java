import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextUI {
    private Menu menu = new Menu();
    private Demultiplexer demultiplexer;


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
        opcoes.add("Estacionamento de trotinete.");
        menu.setOptions(opcoes);
        menu.setHandlers(1,this::listarTrotinetesLivres);
        menu.setHandlers(2,this::estacionamentodeTrotinetes);
        menu.run();
    }

    public void run() throws IOException, InterruptedException {
        Menu();
        demultiplexer.close();
    }

    public void efetuarLogin() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
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
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();
                String dados = username+";"+password+";";
                demultiplexer.send(2,dados.getBytes());

                byte [] b = demultiplexer.receive(2);
                String response = new String(b);
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
                System.out.println("Insira o local: ");
                Scanner scanner = new Scanner(System.in);
                String local = scanner.nextLine();
                System.out.println("Insira o raio de distância (km): ");
                int dist = scanner.nextInt();
                String dados = local+";"+dist+";";
                demultiplexer.send(3,dados.getBytes());

                byte [] b = demultiplexer.receive(3);
                String response = new String(b);
                System.out.println(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        t.join();
    }

    public void estacionamentodeTrotinetes() throws InterruptedException{
        Thread t = new Thread(()->{
           try{
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

           }catch (Exception e) {
               e.printStackTrace();
           }
        });
    }


}