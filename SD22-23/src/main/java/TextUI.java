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
        opcoes.add("Listagem de trotinetes livres.");
        menu.setOptions(opcoes);
        menu.setHandlers(1,this::listarTrotinetes);
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

    public void listarTrotinetes(){
        System.out.println("nao implementado");
    }
}