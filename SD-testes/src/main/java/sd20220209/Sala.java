package sd20220209;

public class Sala {
    private Lista lista;
    private int presentes;
    private int aespera;

    public Sala(){
        this.aespera=0;
        this.presentes=0;
    }

    public Lista getLista() {
        return lista;
    }

    public void setLista(Lista lista) {
        this.lista = lista;
    }

    public int getPresentes() {
        return presentes;
    }

    public void setPresentes(int presentes) {
        this.presentes = presentes;
    }

    public int getAespera() {
        return aespera;
    }

    public void setAespera(int aespera) {
        this.aespera = aespera;
    }

    public void adicionaMembro(){
        this.presentes++;
    }
    public void removeMembro(){
        this.presentes--;
    }

    public void removeEspera(){
        this.aespera--;
    }

    public void adicionaEspera(){
        this.aespera++;
    }
}
