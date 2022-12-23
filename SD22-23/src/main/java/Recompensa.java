import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Recompensa {

    private String start;                   // Name of the starting point
    private String end;                     // Destination's name
    private int reward;                  // Reward given when completing the trip

    // CONSTRUCTORS
    public Recompensa() {}
    public Recompensa(String st, String finish, int prize) {
        this.start = st;
        this.end = finish;
        this.reward = prize;
    }

    // GETTERS AND SETTERS
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    // TOSTRING / EQUALS / HASHCODE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recompensa that = (Recompensa) o;
        return Double.compare(that.reward, reward) == 0 && start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, reward);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append("Start: ").append(this.start).append("\n");
        str.append("Finish: ").append(this.end).append("\n");
        str.append("Reward: ").append(this.reward).append("\n");

        return str.toString();
    }
}
