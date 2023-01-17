package JAVA_RMI;

import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDate;
import java.util.Scanner;

public class Cliente {
    public static void main(String [] args){
            try {
                //obtains the stub for the registry
                Registry registry= LocateRegistry.getRegistry("127.0.0.1",4444);
                //obtain the stub for the remote object from the server's registry
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("Digite o id do filme a consultar:");
                    String name = scanner.nextLine();
                    IMOVIE m = null;
                    try {
                        m = (IMOVIE) registry.lookup(name);//name reference
                    } catch (NotBoundException n) {
                        System.out.println("O filme digitado não existe.");
                    }
                    //invoke remote method
                    if (m != null) {
                        String response = m.getName();
                        double rate = m.getRate();
                        LocalDate date = m.getDate();
                        System.out.println("Movie name: " + response + "\nRate:" + rate + "\nData:" + date);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
