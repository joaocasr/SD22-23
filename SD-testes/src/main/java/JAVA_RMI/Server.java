package JAVA_RMI;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.time.LocalDate;

public class Server {
    public static void main(String args[]) {

        try {
            Registry registry = LocateRegistry.createRegistry(4444);

            Movie m1 = new Movie("HARRY POTTER AND THE ORDER OF THE PHOENIX",6.7, LocalDate.of(2007,07,11));
            Movie m2 = new Movie("HARRY POTTER AND THE HALF BLOOD PRINCE",6.5, LocalDate.of(2009,07,16));

            registry.bind("filme1", m1);
            registry.bind("filme2", m2);

            System.out.println("Server ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}