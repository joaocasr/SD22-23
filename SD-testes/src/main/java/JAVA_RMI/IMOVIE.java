package JAVA_RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface IMOVIE extends Remote {
    public String getName() throws RemoteException;
    public double getRate() throws RemoteException;
    public LocalDate getDate() throws RemoteException;
}
