import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Reserva {
    private Map<String,String> reservas = new HashMap<>(); // cod.reserva-> cod.trotinete
    private Map<String, LocalDateTime> tempoReservas = new HashMap<>();


    public Reserva(){
        this.reservas = new HashMap<>();
        this.tempoReservas = new HashMap<>();
        reservas.put("R01X","C01X");
        tempoReservas.put("R01X",LocalDateTime.now());
    }

    public Map<String, String> getReservas() {
        return reservas;
    }

    public void setReservas(Map<String, String> reservas) {
        this.reservas = reservas;
    }

    public Map<String, LocalDateTime> getTempoReservas() {
        return tempoReservas;
    }

    public void setTempoReservas(Map<String, LocalDateTime> tempoReservas) {
        this.tempoReservas = tempoReservas;
    }
}
