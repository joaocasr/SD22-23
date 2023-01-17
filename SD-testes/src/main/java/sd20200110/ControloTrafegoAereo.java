package sd20200110;

interface ControloTrafegoAereo {
    int pedirParaDescolar();
    int pedirParaAterrar();
    void descolou(int pista);
    void aterrou(int pista);
}