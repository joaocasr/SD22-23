package sd20200915;

interface SalaDeEspera {
    boolean espera(String nome);
    void desiste(String nome);
    String atende();
}