import java.net.InetAddress;
import java.util.Scanner;

public class Client {
    private Menu menu;
    private static ServerWithWorkers serverWithWorkers;
    public static void main(String [] args){

    }

    public static void efetuarLogin() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                System.out.println("Insira o username: ");
                Scanner scanner = new Scanner(System.in);
                String username = scanner.nextLine();
                System.out.println("Insira a password: ");
                String password = scanner.nextLine();


        } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}