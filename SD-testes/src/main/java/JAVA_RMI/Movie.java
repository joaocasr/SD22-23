package JAVA_RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;

public class Movie extends UnicastRemoteObject implements IMOVIE{
    private String title;
    private double rate;
    private LocalDate year;

    public Movie(String title, double rate, LocalDate year) throws RemoteException {
        super();
        this.title = title;
        this.rate = rate;
        this.year = year;
    }

    @Override
    public String getName() throws RemoteException {
        return this.title;
    }

    @Override
    public double getRate() throws RemoteException {
        return this.rate;
    }

    @Override
    public LocalDate getDate() throws RemoteException {
        return this.year;
    }
}
