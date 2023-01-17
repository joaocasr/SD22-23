package sd20220209;

import java.util.ArrayList;
import java.util.List;

public class Lista {
    private int designacao;
    private List<Membro> equipa = new ArrayList<>();

    public Lista(int lista){
        this.designacao=lista;
    }

    public int getDesignacao() {
        return designacao;
    }

    public void setDesignacao(int designacao) {
        this.designacao = designacao;
    }

    public List<Membro> getEquipa() {
        return equipa;
    }

    public void setEquipa(List<Membro> equipa) {
        this.equipa = equipa;
    }
}
